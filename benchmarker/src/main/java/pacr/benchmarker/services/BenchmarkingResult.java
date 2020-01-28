package pacr.benchmarker.services;

import java.util.Map;

public class BenchmarkingResult {

    private Map<String, Benchmark> benchmarks;
    private String globalError;

    public Map<String, Benchmark> getBenchmarks() {
        return benchmarks;
    }

    public String getGlobalError() {
        return globalError;
    }
}
