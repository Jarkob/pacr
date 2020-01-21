package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;

/**
 * Allows benchmarking results to be saved in the system.
 */
public interface IResultSaver {

    /**
     * Saves the benchmarking result for a certain commit in the system and updates other components.
     * Any result for the commit that has already been saved will be replaced by this new result.
     * @param benchmarkingResult the results to be saved. Cannot be null.
     */
    void saveBenchmarkingResults(@NotNull IBenchmarkingResult benchmarkingResult);

}
