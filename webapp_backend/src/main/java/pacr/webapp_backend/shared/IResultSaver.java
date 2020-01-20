package pacr.webapp_backend.shared;

import javassist.NotFoundException;

/**
 * Allows benchmarking results to be saved in the system.
 */
public interface IResultSaver {

    /**
     * Saves benchmarking results in the system and updates other components.
     * @param benchmarkingResult the results to be saved. Cannot be null.
     */
    void saveBenchmarkingResults(IBenchmarkingResult benchmarkingResult);

}
