package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Can import benchmarking results for commits into the system.
 */
public interface IResultImporter {
    /**
     * Saves all the given benchmarking results and any benchmarks that are not already in the system.
     * Replaces any results in the system that belong to commits that are included in this import.
     * @param results the results to import. Cannot be null.
     */
    void importBenchmarkingResults(@NotNull Collection<IBenchmarkingResult> results);
}
