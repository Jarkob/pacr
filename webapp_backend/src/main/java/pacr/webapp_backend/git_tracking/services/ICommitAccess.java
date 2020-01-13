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
     * Stores a commit.
     * @param commit is the commit being stored.
     */
    void addCommit(Commit commit);

    /**
     * Returns all commits belonging to a repository.
     * @param repositoryID is the ID of the repository.
     * @return all commits belonging to the repository.
     */
    Collection<Commit> getAllCommits(int repositoryID);

}
