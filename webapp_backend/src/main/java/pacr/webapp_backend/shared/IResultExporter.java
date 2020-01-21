package pacr.webapp_backend.shared;

import java.util.List;

/**
 * Exports all saved benchmarking results in the system.
 */
public interface IResultExporter {

    /**
     * @return Gets all saved benchmarking results with measurements and/or error messages for commits.
     */
    List<? extends IBenchmarkingResult> exportAllBenchmarkingResults();
}
