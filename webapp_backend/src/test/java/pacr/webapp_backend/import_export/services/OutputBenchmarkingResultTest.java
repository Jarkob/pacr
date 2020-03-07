package pacr.webapp_backend.import_export.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.import_export.servies.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.IBenchmarkingResult;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OutputBenchmarkingResultTest {

    private static final String REPO_URL = "repoUrl";
    private static final String REPO_NAME = "repoName";

    private OutputBenchmarkingResult benchmarkingResult;

    private Collection<IBenchmarkingResult> results;

    private Set<String> trackedBranches;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        this.results = new ArrayList<>();
        this.trackedBranches = new HashSet<>();

        this.benchmarkingResult = new OutputBenchmarkingResult(results, REPO_URL, REPO_NAME, trackedBranches);
    }

    @Test
    void OutputBenchmarkingResult_noArgs() {
        assertDoesNotThrow(() -> {
            OutputBenchmarkingResult benchmarkingResult = new OutputBenchmarkingResult();
        });
    }

    @Test
    void OutputBenchmarkingResult_withArgs() {
        assertThrows(NullPointerException.class, () -> {
           OutputBenchmarkingResult benchmarkingResult =
                   new OutputBenchmarkingResult(null, REPO_URL, REPO_NAME, trackedBranches);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            OutputBenchmarkingResult benchmarkingResult =
                    new OutputBenchmarkingResult(results, null, REPO_NAME, trackedBranches);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            OutputBenchmarkingResult benchmarkingResult =
                    new OutputBenchmarkingResult(results, "", REPO_NAME, trackedBranches);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            OutputBenchmarkingResult benchmarkingResult =
                    new OutputBenchmarkingResult(results, " ", REPO_NAME, trackedBranches);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            OutputBenchmarkingResult benchmarkingResult =
                    new OutputBenchmarkingResult(results, REPO_URL, null, trackedBranches);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            OutputBenchmarkingResult benchmarkingResult =
                    new OutputBenchmarkingResult(results, REPO_URL, "", trackedBranches);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            OutputBenchmarkingResult benchmarkingResult =
                    new OutputBenchmarkingResult(results, REPO_URL, " ", trackedBranches);
        });

        assertThrows(NullPointerException.class, () -> {
            OutputBenchmarkingResult benchmarkingResult =
                    new OutputBenchmarkingResult(results, REPO_URL, REPO_NAME, null);
        });
    }

    @Test
    void getRepositoryPullUrl_noError() {
        assertEquals(REPO_URL, benchmarkingResult.getRepositoryPullUrl());
    }

    @Test
    void getRepositoryName_noError() {
        assertEquals(REPO_NAME, benchmarkingResult.getRepositoryName());
    }

    @Test
    void getBenchmarkingResults_noError() {
        assertEquals(results, benchmarkingResult.getBenchmarkingResults());
    }

    @Test
    void getTrackedBranches_noError() {
        assertEquals(trackedBranches, benchmarkingResult.getTrackedBranches());
    }
}
