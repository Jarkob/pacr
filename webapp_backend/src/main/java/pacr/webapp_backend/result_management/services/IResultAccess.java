package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.result_management.CommitResult;

import java.util.Collection;
import java.util.List;

/**
 * Saves result related objects in the database and retrieves them.
 */
public interface IResultAccess {
    /**
     * Gets the newest commit results (up to 100) that are saved in the database. The entry date is taken for
     * comparison.
     * @return a list of results that is sorted by entry date in descending order.
     */
    List<CommitResult> getNewestResults();

    /**
     * Gets all saved results for the given commit hashes.
     * @param commitHashes the hashes of the commits.
     * @return the results.
     */
    Collection<CommitResult> getResultsFromCommits(Collection<String> commitHashes);

    /**
     * Gets the saved result of a commit. Returns null if no result is saved for the commit.
     * @param commitHash the hash of the commit.
     * @return the result of the commit.
     */
    CommitResult getResultFromCommit(String commitHash);

    /**
     * Saves the given result for a commit.
     * @param result the result.
     */
    void saveResult(CommitResult result);

    /**
     * Deletes the given result.
     * @param result the result.
     */
    void deleteResult(CommitResult result);
}
