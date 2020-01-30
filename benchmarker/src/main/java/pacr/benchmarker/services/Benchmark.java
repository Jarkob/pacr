package pacr.benchmarker.services;

import java.util.Map;

/**
 * Represents a benchmark.
 */
public class Benchmark {

    private Map<String, BenchmarkProperty> properties;

    public Map<String, BenchmarkProperty> getProperties() {
        return properties;
    }
}
