package pacr.benchmarker.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a benchmarking result.
 */
public class BenchmarkingResult {

    private Map<String, Benchmark> benchmarks;
    private String error;

    public BenchmarkingResult() {
        this.benchmarks = new HashMap<>();
        this.error = "";
    }

    /**
     * @return the benchmarks of the result.
     */
    public Map<String, Benchmark> getBenchmarks() {
        return benchmarks;
    }

    /**
     * @return the global error message.
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
