package pacr.webapp_backend.benchmarker_communication.services;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BenchmarkTest {

    private Benchmark benchmark;

    @BeforeEach
    void setUp() {
        this.benchmark = new Benchmark();
    }

    @Test
    void setProperties_noError() {
        final Map<String, BenchmarkProperty> expectedProperties = new HashMap<>();

        benchmark.setProperties(expectedProperties);

        final Map<String, ? extends IBenchmarkProperty> properties = benchmark.getBenchmarkProperties();

        assertEquals(expectedProperties, properties);
    }

    @Test
    void getBenchmarkProperties_returnsDefaultValues() {
        final Map<String, ? extends IBenchmarkProperty> properties = benchmark.getBenchmarkProperties();

        assertNull(properties);
    }
}
