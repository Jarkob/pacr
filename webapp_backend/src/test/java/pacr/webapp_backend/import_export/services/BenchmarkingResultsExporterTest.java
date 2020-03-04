package pacr.webapp_backend.import_export.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.import_export.servies.Benchmark;
import pacr.webapp_backend.import_export.servies.BenchmarkProperty;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsExporter;
import pacr.webapp_backend.import_export.servies.IExportRepositoryAccess;
import pacr.webapp_backend.import_export.servies.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.IRepository;
import pacr.webapp_backend.shared.IResultExporter;
import pacr.webapp_backend.shared.ISystemEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BenchmarkingResultsExporterTest {

    private static final int REPO_1_ID = 1;
    private static final int REPO_2_ID = 2;
    private static final int REPO_3_ID = 3;
    private static final String GLOBAL_ERROR = "globalError";
    private static final String COMMIT_HASH = "commitHash";
    private static final String REPO_1_NAME = "repo1Name";
    private static final String REPO_1_PULL_URL = "pull1Url";
    private static final String REPO_2_NAME = "repo2Name";
    private static final String REPO_2_PULL_URL = "pull2Url";
    private static final String BENCH_NAME = "benchName";
    private static final String PROPERTY_NAME = "propertyName";

    private BenchmarkingResultsExporter benchmarkingResultsExporter;

    @Mock
    private IResultExporter resultExporter;

    @Mock
    private IExportRepositoryAccess repositoryAccess;

    @Mock
    private IRepository repository1;

    @Mock
    private IRepository repository2;

    @Mock
    private ISystemEnvironment systemEnvironment;

    // no generics because Mockito can't handle it.
    private List benchmarkingResults;
    private HashMap properties;
    private HashMap benchmarks;

    private final int amtBenchmarkingResultsRepo1 = 5;
    private final int amtBenchmarkingResultsRepo2 = 6;
    private final int amtBenchmarkingResultsRepo3 = 2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(repository1.getName()).thenReturn(REPO_1_NAME);
        when(repository1.getPullURL()).thenReturn(REPO_1_PULL_URL);

        when(repository2.getName()).thenReturn(REPO_2_NAME);
        when(repository2.getPullURL()).thenReturn(REPO_2_PULL_URL);

        when(repositoryAccess.findGitRepositoryById(REPO_1_ID)).thenReturn(repository1);
        when(repositoryAccess.findGitRepositoryById(REPO_2_ID)).thenReturn(repository2);
        when(repositoryAccess.findGitRepositoryById(REPO_3_ID)).thenReturn(null);

        this.properties = new HashMap();
        final int amtProperties = 5;
        for (int i = 0; i < amtProperties; i++) {
            final IBenchmarkProperty property = mock(IBenchmarkProperty.class);

            properties.put(PROPERTY_NAME + i, new BenchmarkProperty(property));
        }

        this.benchmarks = new HashMap();

        final int amtBenchmarks = 5;
        for (int i = 0; i < amtBenchmarks; i++) {
            final IBenchmark benchmark = mock(IBenchmark.class);

            when(benchmark.getBenchmarkProperties()).thenReturn(properties);

            this.benchmarks.put(BENCH_NAME + i, new Benchmark(benchmark));
        }

        this.benchmarkingResults = new ArrayList();
        this.benchmarkingResults.addAll(createBenchmarkingResults(amtBenchmarkingResultsRepo1, REPO_1_ID));
        this.benchmarkingResults.addAll(createBenchmarkingResults(amtBenchmarkingResultsRepo2, REPO_2_ID));
        this.benchmarkingResults.addAll(createBenchmarkingResults(amtBenchmarkingResultsRepo3, REPO_3_ID));

        when(resultExporter.exportAllBenchmarkingResults()).thenReturn(benchmarkingResults);

        this.benchmarkingResultsExporter = new BenchmarkingResultsExporter(resultExporter, repositoryAccess);
    }

    private Collection<IBenchmarkingResult> createBenchmarkingResults(int amount, int repoID) {
        Collection<IBenchmarkingResult> results = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            IBenchmarkingResult benchmarkingResult = mock(IBenchmarkingResult.class);
            when(benchmarkingResult.getSystemEnvironment()).thenReturn(systemEnvironment);
            when(benchmarkingResult.getRepositoryID()).thenReturn(repoID);
            when(benchmarkingResult.getGlobalError()).thenReturn(GLOBAL_ERROR + i);
            when(benchmarkingResult.getCommitHash()).thenReturn(COMMIT_HASH + i);
            when(benchmarkingResult.getBenchmarks()).thenReturn(benchmarks);

            results.add(benchmarkingResult);
        }

        return results;
    }

    @Test
    void Benchmark_noArgs() {
        assertDoesNotThrow(() -> {
            Benchmark benchmark = new Benchmark();
        });
    }

    @Test
    void exportBenchmarkingResults_noError() {
        final Collection<OutputBenchmarkingResult> exportedResults = benchmarkingResultsExporter.exportBenchmarkingResults();

        assertNotNull(exportedResults);
        assertEquals(2, exportedResults.size());

        OutputBenchmarkingResult resultRepo1 = null;
        OutputBenchmarkingResult resultRepo2 = null;

        for (final OutputBenchmarkingResult result : exportedResults) {
            if (result.getRepositoryName().equals(REPO_1_NAME)) {
                resultRepo1 = result;
            } else if (result.getRepositoryName().equals(REPO_2_NAME)) {
                resultRepo2 = result;
            } else {
                fail("Unknown Repository was exported.");
            }
        }

        assertNotNull(resultRepo1);
        assertNotNull(resultRepo2);

        assertEquals(amtBenchmarkingResultsRepo1, resultRepo1.getBenchmarkingResults().size());
        assertEquals(amtBenchmarkingResultsRepo2, resultRepo2.getBenchmarkingResults().size());

        assertEquals(REPO_1_PULL_URL, resultRepo1.getRepositoryPullUrl());
        assertEquals(REPO_2_PULL_URL, resultRepo2.getRepositoryPullUrl());
    }
}
