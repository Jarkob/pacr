package pacr.webapp_backend.import_export.servies;

import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

/**
 * Represents a benchmark with multiple properties.
 */
@NoArgsConstructor
public class Benchmark implements IBenchmark {

    private Map<String, BenchmarkProperty> benchmarkProperties;

    /**
     * Creates a Benchmark from an IBenchmark interface.
     *
     * @param benchmark the IBenchmark which is used to create the Benchmark.
     */
    public Benchmark(final IBenchmark benchmark) {
        this.benchmarkProperties = new HashMap<>();
        final Map<String, ? extends IBenchmarkProperty> properties = benchmark.getBenchmarkProperties();

        for (final Map.Entry<String, ? extends IBenchmarkProperty> entry : properties.entrySet()) {
            this.benchmarkProperties.put(entry.getKey(), new BenchmarkProperty(entry.getValue()));
        }
    }

    @Override
    public Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties() {
        return benchmarkProperties;
    }
}
