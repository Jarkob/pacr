package pacr.webapp_backend.import_export.servies;

import java.util.HashMap;
import java.util.Map;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

/**
 * Represents a benchmark with multiple properties.
 */
public class Benchmark implements IBenchmark {

    private Map<String, BenchmarkProperty> benchmarkProperties;

    /**
     * Creates an empty Benchmark.
     *
     * Needed for Spring to work.
     */
    public Benchmark() {
    }

    /**
     * Creates a Benchmark from an IBenchmark interface.
     *
     * @param benchmark the IBenchmark which is used to create the Benchmark.
     */
    public Benchmark(IBenchmark benchmark) {
        this.benchmarkProperties = new HashMap<>();
        Map<String, ? extends IBenchmarkProperty> properties = benchmark.getBenchmarkProperties();

        for (String propertyName : properties.keySet()) {
            this.benchmarkProperties.put(propertyName, new BenchmarkProperty(properties.get(propertyName)));
        }
    }

    @Override
    public Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties() {
        return benchmarkProperties;
    }
}
