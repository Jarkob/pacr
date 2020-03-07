package pacr.webapp_backend.benchmarker_communication.services;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.shared.IBenchmark;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class JobResultTest {

    private static final String GLOBAL_ERROR = "globalError";

    private JobResult jobResult;

    @Mock
    private BenchmarkingResult benchmarkingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        this.jobResult = new JobResult();
    }

    @Test
    void gettersAvailable_returnDefaultValues() {
        assertNull(jobResult.getCommitHash());
        assertNull(jobResult.getRepository());
        assertNull(jobResult.getSystemEnvironment());

        assertEquals(JobResult.BENCHMARKING_RESULT_MISSING_ERROR, jobResult.getGlobalError());

        assertEquals(0, jobResult.getExecutionTime());
        assertEquals(-1, jobResult.getRepositoryID());

        Map<String, ? extends IBenchmark> benchmarks = jobResult.getBenchmarks();
        assertNotNull(benchmarks);
        assertEquals(0, benchmarks.size());
    }

    @Test
    void setBenchmarkingResult_noError() {
        assertDoesNotThrow(() -> {
            jobResult.setBenchmarkingResult(benchmarkingResult);
        });

        assertDoesNotThrow(() -> {
            jobResult.setBenchmarkingResult(null);
        });
    }

    @Test
    void getBenchmarks_benchmarkingResultSet() {
        Map expectedBenchmarks = new HashMap<>();
        when(benchmarkingResult.getBenchmarks()).thenReturn(expectedBenchmarks);

        jobResult.setBenchmarkingResult(benchmarkingResult);

        Map<String, ? extends IBenchmark> benchmarks = jobResult.getBenchmarks();
        assertEquals(expectedBenchmarks, benchmarks);
    }

    @Test
    void getGlobalError_benchmarkingResultSet() {
        final String errorMessage = "error";

        jobResult.setBenchmarkingResult(benchmarkingResult);

        when(benchmarkingResult.getGlobalError()).thenReturn(errorMessage);

        String globalError = jobResult.getGlobalError();
        assertEquals(errorMessage, globalError);

        when(benchmarkingResult.getGlobalError()).thenReturn(null);

        globalError = jobResult.getGlobalError();
        assertNull(globalError);
    }

    @Test
    void getGlobalError_validErrorMessage() {
        jobResult.setBenchmarkingResult(benchmarkingResult);

        when(benchmarkingResult.getGlobalError()).thenReturn(GLOBAL_ERROR);

        String globalError = jobResult.getGlobalError();
        assertEquals(GLOBAL_ERROR, globalError);
    }
}
