package pacr.webapp_backend.shared;

/**
 * Allows benchmarking results to be saved in the system.
 */
public interface IResultSaver {

    /**
     * Saves benchmarking results in the system.
     * @param benchmarkingResult the results to be saved.
     */
    void saveBenchmarkingResults(IBenchmarkingResult benchmarkingResult);

}
