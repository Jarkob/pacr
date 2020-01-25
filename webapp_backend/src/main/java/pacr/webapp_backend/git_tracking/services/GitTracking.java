package pacr.webapp_backend.git_tracking.services;

import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.git.GitHandler;
import pacr.webapp_backend.shared.IJobScheduler;
import pacr.webapp_backend.shared.IRepositoryImporter;
import pacr.webapp_backend.shared.IResultDeleter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the Git Tracking component of the system.
 * Manages repositories, commits, branches and can pull from them.
 *
 * @author Pavel Zwerschke
 */
@Component
public class GitTracking implements IRepositoryImporter {

    private static final Logger LOGGER = LogManager.getLogger(GitTracking.class);

    private IGitTrackingAccess gitTrackingAccess;
    private IResultDeleter resultDeleter;
    private IJobScheduler jobScheduler;
    private IColorPicker colorPicker;
    private GitHandler gitHandler;

    /**
     * Initiates an instance of GitTracking.
     * @param gitTrackingAccess is the database access for repositories and commits.
     * @param gitHandler is the interface that manages the JGit integration.
     * @param resultDeleter is the interface that deletes benchmarking results.
     * @param jobScheduler is the interface that adds job to the benchmarking queue.
     * @param colorPicker is the algorithm that assigns new colors to repositories.
     */
    public GitTracking(@NotNull IGitTrackingAccess gitTrackingAccess, @NotNull GitHandler gitHandler,
                       @NotNull IResultDeleter resultDeleter, @NotNull IJobScheduler jobScheduler,
                       @NotNull IColorPicker colorPicker) {
        Objects.requireNonNull(gitTrackingAccess);
        Objects.requireNonNull(gitHandler);
        Objects.requireNonNull(resultDeleter);
        Objects.requireNonNull(jobScheduler);
        Objects.requireNonNull(colorPicker);

        this.gitTrackingAccess = gitTrackingAccess;
        this.gitHandler = gitHandler;
        this.resultDeleter = resultDeleter;
        this.jobScheduler = jobScheduler;
        this.colorPicker = colorPicker;
    }

    /**
     * Adds a repository.
     * @param repositoryURL is the pull URL of the repository.
     * @param observeFromDate is the date from when it should be observed.
     * @param name is the name of the repository.
     * @return the ID of the repository.
     */
    @Override
    public int addRepository(@NotNull String repositoryURL, LocalDate observeFromDate, @NotNull String name,
                             @NotNull Collection<String> branchNames) {
        Objects.requireNonNull(repositoryURL);
        Objects.requireNonNull(name);

        GitRepository repository = new GitRepository(false, new HashSet<>(),
                repositoryURL, name, colorPicker.getNextColor(), observeFromDate);

        for (String branchName : branchNames) {
            GitBranch branch = new GitBranch(branchName);
            repository.addBranchToSelection(branch);
        }
        return gitTrackingAccess.addRepository(repository);
    }

    /**
     * Returns all repositories.
     * @return collection of repositories.
     */
    public Collection<GitRepository> getAllRepositories() {
        return gitTrackingAccess.getAllRepositories();
    }

    /**
     * Removes a repository.
     * @param repositoryID is the ID of the repository.
     * @throws NotFoundException if the repository was not found in the database.
     */
    public void removeRepository(int repositoryID) throws NotFoundException {
        LOGGER.info("Removing repository with ID {}.", repositoryID);
        for (GitCommit commit : gitTrackingAccess.getAllCommits(repositoryID)) {
            resultDeleter.deleteBenchmarkingResults(commit.getCommitHash());
        }

        gitTrackingAccess.removeRepository(repositoryID);
    }

    /**
     * Updates a repository.
     * @param repository is the repository being updated.
     * @throws NotFoundException if the repository was not found in the database.
     */
    public void updateRepository(@NotNull GitRepository repository) throws NotFoundException {
        Objects.requireNonNull(repository);

        gitTrackingAccess.updateRepository(repository);
    }

    /**
     * Pulls from a repository and adds new commits to the job scheduling.
     * @param repositoryID is the ID of the repository.
     * @throws NotFoundException when the repository was not found.
     */
    public synchronized void pullFromRepository(int repositoryID) throws NotFoundException {
        GitRepository gitRepository = gitTrackingAccess.getRepository(repositoryID);
        if (gitRepository == null) {
            throw new NotFoundException("Repository with ID " + repositoryID + " was not found.");
        }

        Collection<GitCommit> untrackedCommits = gitHandler.pullFromRepository(gitRepository);
        LOGGER.info("Got {} untracked commits.", untrackedCommits.size());

        // automatically adds all new commits to the database
        gitTrackingAccess.updateRepository(gitRepository);
        for (GitCommit commit : untrackedCommits) {
            // add job to queue
            jobScheduler.addJob(gitRepository.getPullURL(), commit.getCommitHash());
        }

        LOGGER.info("Finished with pulling from repository {} ({}).", gitRepository.getName(), repositoryID);
    }

    /**
     * Pulls from all repositories.
     */
    public void pullFromAllRepositories() {
        for (GitRepository repository : gitTrackingAccess.getAllRepositories()) {
            if (!repository.isHookSet()) {
                try {
                    pullFromRepository(repository.getId());
                } catch (NotFoundException e) {
                    throw new RuntimeException("Repository should be found.");
                }
            }
        }
    }
}
