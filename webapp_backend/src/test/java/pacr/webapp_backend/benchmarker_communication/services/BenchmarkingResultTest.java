package pacr.webapp_backend.benchmarker_communication.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BenchmarkingResultTest {

    private BenchmarkingResult benchmarkingResult;

    @BeforeEach
    void setUp() {
        this.benchmarkingResult = new BenchmarkingResult();
    }

    @Test
    void gettersAvailable_returnDefaultValues() {
        assertNull(benchmarkingResult.getGlobalError());
        assertNull(benchmarkingResult.getBenchmarks());
    }

    @Test
    void setGlobalError_noError() {
        final String expectedError = "error";

        benchmarkingResult.setGlobalError(expectedError);
        String error = benchmarkingResult.getGlobalError();
        assertEquals(expectedError, error);
    }

    @Test
    void getGlobalError_invalidError() {
        benchmarkingResult.setGlobalError("");
        String error = benchmarkingResult.getGlobalError();
        assertNull(error);

        benchmarkingResult.setGlobalError(" ");
        error = benchmarkingResult.getGlobalError();
        assertNull(error);

        benchmarkingResult.setGlobalError(null);
        error = benchmarkingResult.getGlobalError();
        assertNull(error);
    }
}
