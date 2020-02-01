package pacr.webapp_backend.git_tracking.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.git.GitHandler;
import pacr.webapp_backend.shared.IJobScheduler;
import pacr.webapp_backend.shared.IRepositoryImporter;
import pacr.webapp_backend.shared.IResultDeleter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Collection;

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
                             @NotNull Set<String> branchNames) {
        Objects.requireNonNull(repositoryURL);
        Objects.requireNonNull(name);

        GitRepository repository = new GitRepository();
        repository.setTrackAllBranches(false);
        repository.setPullURL(repositoryURL);
        repository.setName(name);
        repository.setObserveFromDate(observeFromDate);
        repository.setSelectedBranches(branchNames);
        repository.setColor(colorPicker.getNextColor());

        return gitTrackingAccess.addRepository(repository);
    }

    /**
     * Gets all commits belonging to the repository in pages.
     * @param pageable contains information about the page.
     * @param repositoryID the id of the repository.
     * @return a page of commits.
     */
    public Page<GitCommit> getAllCommits(int repositoryID, Pageable pageable) {
        return gitTrackingAccess.getAllCommits(repositoryID, pageable);
    }

    /**
     * Returns all repositories.
     * @return collection of repositories.
     */
    public Set<GitRepository> getAllRepositories() {
        return gitTrackingAccess.getAllRepositories();
    }

    /**
     * Removes a repository.
     * @param repositoryID is the ID of the repository.
     * @throws NoSuchElementException if the repository was not found in the database.
     */
    public void removeRepository(int repositoryID) throws NoSuchElementException {
        LOGGER.info("Removing repository with ID {}.", repositoryID);
        for (GitCommit commit : gitTrackingAccess.getAllCommits(repositoryID)) {
            resultDeleter.deleteBenchmarkingResults(commit.getCommitHash());
        }

        gitTrackingAccess.removeRepository(repositoryID);
    }

    /**
     * Updates a repository.
     * @param repository is the repository being updated.
     * @throws NoSuchElementException if the repository was not found in the database.
     */
    public void updateRepository(@NotNull GitRepository repository) throws NoSuchElementException {
        Objects.requireNonNull(repository);

        gitTrackingAccess.updateRepository(repository);
    }

    /**
     * Pulls from a repository and adds new commits to the job scheduling.
     * @param repositoryID is the ID of the repository.
     * @throws NoSuchElementException when the repository was not found.
     */
    public synchronized void pullFromRepository(int repositoryID) throws NoSuchElementException {
        GitRepository gitRepository = gitTrackingAccess.getRepository(repositoryID);
        if (gitRepository == null) {
            throw new NoSuchElementException("Repository with ID " + repositoryID + " was not found.");
        }

        gitHandler.setBranchesToRepo(gitRepository);
        gitTrackingAccess.updateRepository(gitRepository);

        gitRepository = gitTrackingAccess.getRepository(repositoryID);

        Collection<String> untrackedCommitHashes = gitHandler.pullFromRepository(gitRepository);
        LOGGER.info("Got {} untracked commits.", untrackedCommitHashes.size());

        // automatically adds all new commits to the database
        gitTrackingAccess.updateRepository(gitRepository);

        // add jobs to scheduler
        jobScheduler.addJobs(gitRepository.getPullURL(), untrackedCommitHashes);

        LOGGER.info("Finished with pulling from repository {} ({}).", gitRepository.getName(), repositoryID);
    }

    /**
     * Pulls from all repositories.
     */
    public void pullFromAllRepositories() {
        for (GitRepository repository : gitTrackingAccess.getAllRepositories()) {
            LOGGER.info("Checking if hook is set for {} ({}).", repository.getName(), repository.getId());
            if (!repository.isHookSet()) {
                LOGGER.info("Trying to pull from repository {} ({}).", repository.getName(), repository.getId());
                pullFromRepository(repository.getId());
            }
        }
    }

    /**
     * Updates the color of a repository.
     * @param repository is the repository.
     * @param color is the color of the repository.
     */
    public void updateColorOfRepository(GitRepository repository, String color) {
        this.colorPicker.setColorToUnused(repository.getColor());
        this.colorPicker.setColorToUsed(color);
        repository.setColor(color);

        gitTrackingAccess.updateRepository(repository);
    }

    /**
     * Returns a repository.
     * @param id is the ID of the repository.
     * @return repository.
     */
    public GitRepository getRepository(int id) {
        return gitTrackingAccess.getRepository(id);
    }

    public Set<String> getBranches(String pullURL) {
        return gitHandler.getBranchesOfRepository(pullURL);
    }
}
