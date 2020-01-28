package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Map;

import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmark;

/**
 * Represents a collection of benchmarks that were run for a commit.
 */
public class BenchmarkingResult {

    private Map<String, Benchmark> benchmarks;
    private String globalError;

    /**
     * Creates an empty BenchmarkingResult.
     *
     * Needed for Spring to work.
     */
    public BenchmarkingResult() {
    }

    /**
     * @return a list of benchmarks that were run associated with their name.
     */
    public Map<String, ? extends IBenchmark> getBenchmarks() {
        return benchmarks;
    }

    /**
     * @return an error message if there was a general error. Null is returned if there was no error.
     */
    public String getGlobalError() {
        if (!StringUtils.hasText(globalError)) {
            return null;
        }

        return globalError;
    }

    /**
     * TODO
     * @param globalError
     * @return
     */
    public void setGlobalError(String globalError) {
        this.globalError = globalError;
    }
}
