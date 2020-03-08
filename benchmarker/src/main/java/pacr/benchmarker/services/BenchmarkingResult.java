package pacr.benchmarker.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a benchmarking result.
 */
public class BenchmarkingResult {

    private Map<String, Benchmark> benchmarks;
    private String globalError;

    public BenchmarkingResult() {
        this.benchmarks = new HashMap<>();
        this.globalError = "";
    }

    public BenchmarkingResult(BenchmarkingResultAdapter adapter) {
        this.benchmarks = adapter.getBenchmarks();
        this.globalError = adapter.getError();
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
    public String getGlobalError() {
        return globalError;
    }

    public void setGlobalError(String globalError) {
        this.globalError = globalError;
    }
}
