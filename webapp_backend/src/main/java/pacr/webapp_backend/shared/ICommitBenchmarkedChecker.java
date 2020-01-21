package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;

/**
 * Checks if the system contains a benchmarking result for a specific commit.
 */
public interface ICommitBenchmarkedChecker {

    /**
     * Checks if there are measurements or benchmarking error messages saved for a commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @return if at least one measurement or benchmarking error message is saved for the commit.
     */
    boolean isCommitBenchmarked(@NotNull String commitHash);
}
