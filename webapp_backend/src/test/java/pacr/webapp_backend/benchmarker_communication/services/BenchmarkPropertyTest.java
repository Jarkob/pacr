package pacr.webapp_backend.benchmarker_communication.services;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.ResultInterpretation;

import static org.junit.jupiter.api.Assertions.*;

public class BenchmarkPropertyTest {

    private BenchmarkProperty benchmarkProperty;

    @BeforeEach
    void setUp() {
        this.benchmarkProperty = new BenchmarkProperty();
    }

    @Test
    void gettersAvailable_returnDefaultValues() {
        assertNull(benchmarkProperty.getError());
        assertNull(benchmarkProperty.getResultInterpretation());
        assertNull(benchmarkProperty.getUnit());
        assertNull(benchmarkProperty.getResults());
    }

    @Test
    void settersAvailable_noError() {
        final String expectedError = "error";
        final String expectedUnit = "unit";
        final ResultInterpretation expectedInterpretation = ResultInterpretation.LESS_IS_BETTER;
        final Collection<Double> expectedResults = new ArrayList<>();

        benchmarkProperty.setUnit(expectedUnit);
        String unit = benchmarkProperty.getUnit();
        assertEquals(expectedUnit, unit);

        benchmarkProperty.setError(expectedError);
        String error = benchmarkProperty.getError();
        assertEquals(expectedError, error);

        benchmarkProperty.setResultInterpretation(expectedInterpretation);
        ResultInterpretation interpretation = benchmarkProperty.getResultInterpretation();
        assertEquals(expectedInterpretation, interpretation);

        benchmarkProperty.setResults(expectedResults);
        Collection<Double> results = benchmarkProperty.getResults();
        assertEquals(expectedResults, results);
    }

    @Test
    void getError_invalidError() {
        benchmarkProperty.setError("");
        String error = benchmarkProperty.getError();
        assertNull(error);

        benchmarkProperty.setError(" ");
        error = benchmarkProperty.getError();
        assertNull(error);

        benchmarkProperty.setError(null);
        error = benchmarkProperty.getError();
        assertNull(error);
    }

    @Test
    void isError_noError() {
        benchmarkProperty.setError("error");
        assertTrue(benchmarkProperty.isError());

        benchmarkProperty.setError("");
        assertFalse(benchmarkProperty.isError());

        benchmarkProperty.setError(" ");
        assertFalse(benchmarkProperty.isError());

        benchmarkProperty.setError(null);
        assertFalse(benchmarkProperty.isError());
    }
}
