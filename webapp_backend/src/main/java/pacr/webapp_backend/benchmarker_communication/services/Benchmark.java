package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

/**
 * Represents a benchmark with multiple properties.
 */
@NoArgsConstructor
public class Benchmark implements IBenchmark {

    @Setter
    private Map<String, BenchmarkProperty> properties;

    /**
     * @return the benchmark properties.
     */
    @Override
    public Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties() {
        return properties;
    }

}
