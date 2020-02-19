package pacr.benchmarker.services;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a benchmark.
 */
public class Benchmark {

    @Getter
    private Map<String, BenchmarkProperty> properties;

    public Benchmark() {
        this.properties = new HashMap<>();
    }
}
