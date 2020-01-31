package pacr.benchmarker.services;

import java.util.Map;

/**
 * Represents a benchmarking result.
 */
public class BenchmarkingResult {

    private Map<String, Benchmark> benchmarks;
    private String globalError;

    /**
     * @return the benchmarks of the result.
     */
    public Map<String, Benchmark> getBenchmarks() {
        return benchmarks;
    }

    /**
     * @return the global error message.
     */
    public String getGlobalError() {
        return globalError;
    }

    public void setGlobalError(String globalError) {
        this.globalError = globalError;
    }
}