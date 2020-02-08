package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.ResultInterpretation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkPropertyTest {
    private static final String PROPERTY_NAME = "property";
    private static final String UNIT = "unit";

    @Test
    void equals_sameObject_shouldReturnTrue() {
        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        assertTrue(property.equals(property));
        assertEquals(property.hashCode(), property.hashCode());
    }

    @Test
    void equals_differentClass_shouldReturnFalse() {
        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        assertFalse(property.equals(new Benchmark()));
    }

    @Test
    void equals_sameAttributes_shouldReturnTrue() {
        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        BenchmarkProperty sameProperty = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);

        assertTrue(property.equals(sameProperty));
        assertEquals(property.hashCode(), sameProperty.hashCode());
    }

    @Test
    void constructor_blankName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new BenchmarkProperty(" ", UNIT, ResultInterpretation.LESS_IS_BETTER));
    }
}
