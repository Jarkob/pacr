package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;

/**
 * Deletes benchmarking results from the system.
 */
public interface IResultDeleter {

    /**
     * Deletes the result for the commit if it is saved in the system.
     * Enters CommitResult.class monitor.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     */
    void deleteBenchmarkingResults(@NotNull String commitHash);

    /**
     * Deletes any saved results for commits from this repository. Nothing happens if no repository with the given id
     * exists in the system.
     * Enters CommitResult.class monitor.
     * @param repositoryID the id of the repository.
     */
    void deleteAllResultsForRepository(int repositoryID);

}
