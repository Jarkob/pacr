package pacr.webapp_backend.git_tracking.services;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.shared.IRepositoryImporter;
import pacr.webapp_backend.shared.IResultDeleter;

import javax.validation.constraints.NotNull;
import java.awt.Color;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents the Git Tracking component of the system.
 * Manages repositories, commits, branches and can pull from them.
 *
 * @author Pavel Zwerschke
 */
@Component
public class GitTracking implements IRepositoryImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitTracking.class);

    private IGitTrackingAccess gitTrackingAccess;

    private IResultDeleter resultDeleter;

    /**
     * Initiates an instance of GitTracking.
     * @param gitTrackingAccess is the database access for repositories and commits.
     */
    public GitTracking(@NotNull IGitTrackingAccess gitTrackingAccess) {
        Objects.requireNonNull(gitTrackingAccess);

        this.gitTrackingAccess = gitTrackingAccess;
    }

    /**
     * Adds a repository.
     * @param repositoryURL is the pull URL of the repository.
     * @param observeFromDate is the date from when it should be observed.
     * @param name is the name of the repository.
     * @return the ID of the repository.
     */
    @Override
    public int addRepository(@NotNull String repositoryURL, LocalDate observeFromDate, @NotNull String name) {
        Objects.requireNonNull(repositoryURL);
        Objects.requireNonNull(name);

        GitRepository repository = new GitRepository(false, new HashSet<>(),
                repositoryURL, name, getNextColor(), null);
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
        gitTrackingAccess.removeRepository(repositoryID);

        for (GitCommit commit : gitTrackingAccess.getAllCommits(repositoryID)) {
            resultDeleter.deleteBenchmarkingResults(commit.getCommitHash());
        }
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
     */
    public void pullFromRepository(int repositoryID) {
        //todo
    }

    /**
     * Pulls from all repositories.
     */
    public void pullFromAllRepositories() {
        for (GitRepository repository : gitTrackingAccess.getAllRepositories()) {
            if (!repository.isHookSet()) {
                pullFromRepository(repository.getId());
            }
        }
    }


    /**
     * Gets the next available color in the color list for repositories.
     * @return next color.
     */
    private Color getNextColor() {
        //todo
        return null;
    }

    /**
     * Sets a color to unused in the color list for repositories.
     * @param color gets set to unused.
     */
    private void setColorToUnused(Color color) {
        //todo
    }

    /**
     * Sets a color to used in the color list for repositories.
     * @param color gets set to used.
     */
    private void setColorToUsed(Color color) {
        //todo
    }
}