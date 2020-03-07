package pacr.webapp_backend.import_export.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.import_export.servies.BenchmarkingResult;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class BenchmarkingResultTest {

    private static final String GLOBAL_ERROR = "globalError";
    private static final String COMMIT_HASH = "commitHash";

    private BenchmarkingResult benchmarkingResult;

    @Mock
    private IBenchmarkingResult benchmarkingResultInterface;

    @Mock
    private ISystemEnvironment systemEnvironment;

    @Mock
    private Map benchmarks;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(benchmarkingResultInterface.getCommitHash()).thenReturn(COMMIT_HASH);
        when(benchmarkingResultInterface.getGlobalError()).thenReturn(GLOBAL_ERROR);
        when(benchmarkingResultInterface.getSystemEnvironment()).thenReturn(systemEnvironment);
        when(benchmarkingResultInterface.getBenchmarks()).thenReturn(benchmarks);

        this.benchmarkingResult = new BenchmarkingResult(benchmarkingResultInterface);
    }

    @Test
    void getCommitHash_noError() {
        assertEquals(COMMIT_HASH, benchmarkingResult.getCommitHash());
    }

    @Test
    void getGlobalError_noError() {
        assertEquals(GLOBAL_ERROR, benchmarkingResult.getGlobalError());
    }

    @Test
    void getRepositoryID_noError() {
        assertEquals(-1, benchmarkingResult.getRepositoryID());
    }

    @Test
    void getSystemEnvironment_noError() {
        assertNotNull(benchmarkingResult.getSystemEnvironment());
    }

    @Test
    void getBenchmarks_noError() {
        assertNotNull(benchmarkingResult.getBenchmarks());
    }
}
