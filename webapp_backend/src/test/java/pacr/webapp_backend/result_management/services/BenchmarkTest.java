package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkTest {

    private static final String BENCHMARK_NAME = "benchmark";
    private static final String GROUP_NAME = "group";

    @Test
    void equals_sameObject_shouldReturnTrue() {
        final Benchmark benchmark = new Benchmark(BENCHMARK_NAME);

        assertTrue(benchmark.equals(benchmark));
        assertEquals(benchmark.hashCode(), benchmark.hashCode());
    }

    @Test
    void equals_differentClass_shouldReturnFalse() {
        final Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        final BenchmarkProperty property = new BenchmarkProperty();

        assertFalse(benchmark.equals(property));
    }

    @Test
    void equals_sameAttributes_shouldReturnTrue() {
        final Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        final Benchmark sameBenchmark = new Benchmark(BENCHMARK_NAME);

        final BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        benchmark.setGroup(group);
        sameBenchmark.setGroup(group);

        assertTrue(benchmark.equals(sameBenchmark));
        assertEquals(benchmark.hashCode(), sameBenchmark.hashCode());
    }

    @Test
    void constructor_nameIsBlank_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Benchmark(" "));
    }
}
