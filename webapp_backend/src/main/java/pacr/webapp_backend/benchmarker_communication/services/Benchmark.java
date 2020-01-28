package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Map;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

/**
 * Represents a benchmark with multiple properties.
 */
public class Benchmark implements IBenchmark {

    private Map<String, BenchmarkProperty> properties;

    /**
     * Creates an empty Benchmark.
     *
     * Needed for Spring to work.
     */
    public Benchmark() {
    }

    @Override
    public Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties() {
        return properties;
    }

    /**
     * TODO
     * @param properties
     */
    public void setProperties(Map<String, BenchmarkProperty> properties) {
        this.properties = properties;
    }
}
