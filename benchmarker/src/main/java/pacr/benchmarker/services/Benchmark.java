package pacr.benchmarker.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a benchmark.
 */
public class Benchmark {

    private Map<String, BenchmarkProperty> properties;

    public Benchmark() {
        this.properties = new HashMap<>();
    }

    public Map<String, BenchmarkProperty> getProperties() {
        return properties;
    }
}
