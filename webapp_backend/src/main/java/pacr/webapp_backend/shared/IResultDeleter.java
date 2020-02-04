package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Deletes benchmarking results from the system.
 */
public interface IResultDeleter {

    /**
     * Deletes the results for the commits.
     * Enters CommitResult.class monitor.
     * @param commitHashes the hashes of the commits. Cannot be null.
     */
    void deleteBenchmarkingResults(@NotNull Collection<String> commitHashes);

    /**
     * Deletes any saved results for commits from this repository. Nothing happens if no repository with the given id
     * exists in the system.
     * Enters CommitResult.class monitor.
     * @param repositoryID the id of the repository.
     */
    @Deprecated
    void deleteAllResultsForRepository(int repositoryID);

}
