package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BenchmarkResultTest {

    @Test
    void constructor_emptyList_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkResult(new HashSet<>(), new Benchmark()));
    }
}
