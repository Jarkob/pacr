package pacr.webapp_backend.shared;

import javassist.NotFoundException;

import java.util.Collection;

/**
 * Can import benchmarking results for commits into the system.
 */
public interface IResultImporter {
    /**
     * Saves all the given benchmarking results and any benchmarks that are not already in the system.
     * @param results the results to import. Cannot be null.
     */
    void importBenchmarkingResults(Collection<IBenchmarkingResult> results);
}
