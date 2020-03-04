package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkGroupTest {
    private static final String GROUP_NAME = "group";

    @Test
    void equals_sameObject_shouldReturnTrue() {
        final BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        assertTrue(group.equals(group));
        assertEquals(group.hashCode(), group.hashCode());
    }

    @Test
    void equals_differentClass_shouldReturnFalse() {
        final BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        final Benchmark benchmark = new Benchmark();

        assertFalse(group.equals(benchmark));
    }

    @Test
    void equals_sameAttributes_shouldReturnTrue() {
        final BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        final BenchmarkGroup sameGroup = new BenchmarkGroup(GROUP_NAME);

        assertTrue(group.equals(sameGroup));
        assertEquals(group.hashCode(), sameGroup.hashCode());
    }

    @Test
    void constructor_blankName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkGroup(" "));
    }
}
