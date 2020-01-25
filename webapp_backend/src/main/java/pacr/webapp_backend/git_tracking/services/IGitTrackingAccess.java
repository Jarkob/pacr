package pacr.webapp_backend.git_tracking.services;

import javassist.NotFoundException;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import java.util.Collection;

/**
 * Represents the interface for storing and accessing
 * the data for Git Tracking.
 *
 * @author Pavel Zwerschke
 */
public interface IGitTrackingAccess {

    /**
     * Returns all repositories.
     * @return repositories.
     */
    Collection<GitRepository> getAllRepositories();

    /**
     * Finds a repository by its repositoryID.
     * @param repositoryID is the repositoryID.
     * @return repository if it was found or else null.
     */
    GitRepository getRepository(int repositoryID);

    /**
     * Stores a repository.
     * @param repository is the repository being stored.
     * @return the ID of the repository.
     */
    int addRepository(GitRepository repository);

    /**
     * Deletes a repository.
     * @param repositoryID is the ID of the repository being deleted.
     * @throws NotFoundException when the repository was not found.
     */
    void removeRepository(int repositoryID) throws NotFoundException;

    /**
     * Updates a repository.
     * @param repository is the repository being updated.
     * @throws NotFoundException when the repository was not found.
     */
    void updateRepository(GitRepository repository) throws NotFoundException;

    /**
     * Stores a commit. The repository to which the commit
     * belongs to must already be stored already in the database.
     * @param commit is the commit being stored.
     */
    void addCommit(GitCommit commit);

    /**
     * Returns all commits belonging to a repository.
     * @param repositoryID is the ID of the repository.
     * @return all commits belonging to the repository.
     */
    Collection<GitCommit> getAllCommits(int repositoryID);

    /**
     * Returns a commit.
     * @param commitHash is the commit hash of the commit.
     * @return the commit with this commit hash or null if not found.
     */
    GitCommit getCommit(String commitHash);

    /**
     * Removes a commit.
     * @param commitHash is the commit hash of the commit.
     */
    void removeCommit(String commitHash);

    /**
     * Checks if a commit is in the database or not.
     * @param commitHash is the commit hash.
     * @return true if the commit is already in the database,
     * false if not.
     */
    boolean containsCommit(String commitHash);

    /**
     * Returns all commit hashes of a repository.
     * @param repositoryID is the ID of the repository.
     * @return all commits hashes of the repository.
     */
    Collection<String> getAllCommitHashes(int repositoryID);
}
