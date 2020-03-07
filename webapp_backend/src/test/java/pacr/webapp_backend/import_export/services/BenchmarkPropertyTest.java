package pacr.webapp_backend.import_export.services;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.import_export.servies.BenchmarkProperty;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class BenchmarkPropertyTest {

    private static final String UNIT = "unit";
    private static final String ERROR = "error";

    private BenchmarkProperty benchmarkProperty;

    @Mock
    private IBenchmarkProperty benchmarkPropertyInterface;

    @Mock
    private ResultInterpretation resultInterpretation;

    private Collection<Double> results;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        results = new ArrayList<>();

        when(benchmarkPropertyInterface.getError()).thenReturn(ERROR);
        when(benchmarkPropertyInterface.getUnit()).thenReturn(UNIT);
        when(benchmarkPropertyInterface.getResultInterpretation()).thenReturn(resultInterpretation);
        when(benchmarkPropertyInterface.getResults()).thenReturn(results);

        this.benchmarkProperty = new BenchmarkProperty(benchmarkPropertyInterface);
    }

    @Test
    void BenchmarkProperty_noArgs() {
        assertDoesNotThrow(() -> {
            BenchmarkProperty benchmarkProperty = new BenchmarkProperty();
        });
    }

    @Test
    void getError_noError() {
        assertEquals(ERROR, benchmarkProperty.getError());
    }

    @Test
    void getUnit_noError() {
        assertEquals(UNIT, benchmarkProperty.getUnit());
    }

    @Test
    void getResultInterpretation_noError() {
        assertEquals(resultInterpretation, benchmarkProperty.getResultInterpretation());
    }

    @Test
    void getResults_noError() {
        assertEquals(results, benchmarkProperty.getResults());
    }

    @Test
    void isError_noError() {
        assertTrue(benchmarkProperty.isError());

        when(benchmarkPropertyInterface.getError()).thenReturn("");
        benchmarkProperty = new BenchmarkProperty(benchmarkPropertyInterface);
        assertFalse(benchmarkProperty.isError());

        when(benchmarkPropertyInterface.getError()).thenReturn(" ");
        benchmarkProperty = new BenchmarkProperty(benchmarkPropertyInterface);
        assertFalse(benchmarkProperty.isError());

        when(benchmarkPropertyInterface.getError()).thenReturn(null);
        benchmarkProperty = new BenchmarkProperty(benchmarkPropertyInterface);
        assertFalse(benchmarkProperty.isError());
    }
}
