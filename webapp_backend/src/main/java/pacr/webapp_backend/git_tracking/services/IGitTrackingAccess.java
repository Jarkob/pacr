package pacr.webapp_backend.git_tracking.services;

import javassist.NotFoundException;
import pacr.webapp_backend.git_tracking.Commit;
import pacr.webapp_backend.git_tracking.Repository;

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
    Collection<Repository> getAllRepositories();

    /**
     * Finds a repository by its repositoryID.
     * @param repositoryID is the repositoryID.
     * @return repository if it was found or else null.
     */
    Repository getRepository(int repositoryID);

    /**
     * Stores a repository.
     * @param repository is the repository being stored.
     * @return the ID of the repository.
     */
    int addRepository(Repository repository);

    /**
     * Deletes a repository.
     * @param repositoryID is the ID of the repository being deleted.
     */
    void removeRepository(int repositoryID) throws NotFoundException;

    /**
     * Updates a repository.
     * @param repository is the repository being updated.
     */
    void updateRepository(Repository repository) throws NotFoundException;

    /**
     * Stores a commit. The repository to which the commit
     * belongs to must already be stored already in the database.
     * @param commit is the commit being stored.
     */
    void addCommit(Commit commit);

    /**
     * Returns all commits belonging to a repository.
     * @param repositoryID is the ID of the repository.
     * @return all commits belonging to the repository.
     */
    Collection<Commit> getAllCommits(int repositoryID);

    /**
     * Returns a commit.
     * @param commitHash is the commit hash of the commit.
     * @return the commit with this commit hash or null if not found.
     */
    Commit getCommit(String commitHash);

    /**
     * Removes a commit.
     * @param commitHash is the commit hash of the commit.
     */
    void removeCommit(String commitHash);

}
