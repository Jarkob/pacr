package pacr.webapp_backend.git_tracking.services;

import pacr.webapp_backend.git_tracking.Commit;

import java.util.Collection;

/**
 * This is the interface for storing and getting Commits.
 *
 * @author Pavel Zwerschke
 */
public interface ICommitAccess {

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
