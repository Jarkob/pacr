package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.ICommit;

import java.util.Collection;
import java.util.List;

/**
 * Retrieves ICommits from the database. Cannot alter the database.
 */
public interface IGetCommitAccess {
    /**
     * Gets the specified amount (or fewer) of the newest commits, that are saved in the database. The entry date is
     * taken for comparison.
     * @param amount the maximum amount of commits.
     * @return a list of commits that is sorted by entry date in ascending order.
     */
    List<? extends ICommit> getNewestCommits(int amount);

    /**
     * Gets all saved commits from a repository. Returns null if no such repository exists.
     * @param id the repository id.
     * @return the commits of the repository.
     */
    Collection<? extends ICommit> getCommitsFromRepository(int id);

    /**
     * Gets all saved commits from a branch of a repository. Returns null if no such repository or branch exists.
     * @param id the repository id.
     * @param branch the branch name.
     * @return the commits of the branch.
     */
    Collection<? extends ICommit> getCommitsFromBranch(int id, String branch);

    /**
     * Gets all saved commits. Returns null if no commits are saved.
     * @return all commits.
     */
    Collection<? extends ICommit> getAllCommits();

    /**
     * Gets the saved commit of the hash. Returns null if no such commit is saved.
     * @param commitHash the commit hash
     * @return the commit.
     */
    ICommit getCommit(String commitHash);
}
