package pacr.webapp_backend.import_export.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.import_export.servies.Benchmark;
import pacr.webapp_backend.import_export.servies.BenchmarkProperty;
import pacr.webapp_backend.import_export.servies.BenchmarkingResult;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsImporter;
import pacr.webapp_backend.import_export.servies.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.IRepositoryImporter;
import pacr.webapp_backend.shared.IResultImporter;
import pacr.webapp_backend.shared.ISystemEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BenchmarkingResultsImporterTest {

    private static final String COMMIT_HASH = "commitHash";
    private static final String GLOBAL_ERROR = "globalError";
    private static final String REPO_NAME = "repoName";
    private static final String REPO_PULL_URL = "pullUrl";
    private static final String BENCH_NAME = "benchName";
    private static final String PROPERTY_NAME = "propertyName";

    private BenchmarkingResultsImporter benchmarkingResultsImporter;

    @Mock
    private IResultImporter resultImporter;

    @Mock
    private IRepositoryImporter repositoryImporter;

    @Mock
    private ISystemEnvironment systemEnvironment;

    // no generic because mockito couldn't handle it.
    private HashMap benchmarks;
    private HashMap properties;

    private Collection<OutputBenchmarkingResult> importedResults;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        this.properties = new HashMap();
        final int amtProperties = 5;
        for (int i = 0; i < amtProperties; i++) {
            IBenchmarkProperty property = mock(IBenchmarkProperty.class);

            properties.put(PROPERTY_NAME + i, new BenchmarkProperty(property));
        }

        this.benchmarks = new HashMap();

        final int amtBenchmarks = 5;
        for (int i = 0; i < amtBenchmarks; i++) {
            IBenchmark benchmark = mock(IBenchmark.class);

            when(benchmark.getBenchmarkProperties()).thenReturn(properties);

            this.benchmarks.put(BENCH_NAME + i, new Benchmark(benchmark));
        }

        this.importedResults = new ArrayList<>();

        final int amtImportedResults = 10;
        for (int i = 0; i < amtImportedResults; i++) {

            Collection<IBenchmarkingResult> benchmarkingResults = new ArrayList<>();
            final int amtBenchmarkingResults = 10;
            for (int j = 0; j < amtBenchmarkingResults; j++) {
                BenchmarkingResult benchmarkingResult = spy(BenchmarkingResult.class);

                when(benchmarkingResult.getCommitHash()).thenReturn(COMMIT_HASH + i + j);
                when(benchmarkingResult.getGlobalError()).thenReturn(GLOBAL_ERROR + i + j);
                when(benchmarkingResult.getRepositoryID()).thenReturn(i * amtBenchmarkingResults + j);
                when(benchmarkingResult.getSystemEnvironment()).thenReturn(systemEnvironment);

                when(benchmarkingResult.getBenchmarks()).thenReturn(benchmarks);

                benchmarkingResults.add(benchmarkingResult);
            }

            this.importedResults.add(new OutputBenchmarkingResult(benchmarkingResults, REPO_PULL_URL + i,
                    REPO_NAME + i, new HashSet<>()));
        }

        this.benchmarkingResultsImporter = new BenchmarkingResultsImporter(resultImporter, repositoryImporter);
    }

    @Test
    void importBenchmarkingResults_noError() {
        ArgumentCaptor<String> pullURLCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> repoNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDate> observeFromDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<HashSet> selectedBranchesCaptor = ArgumentCaptor.forClass(HashSet.class);

        LocalDate now = LocalDate.now();
        benchmarkingResultsImporter.importBenchmarkingResults(importedResults);

        verify(repositoryImporter, times(importedResults.size())).importRepository(pullURLCaptor.capture(), observeFromDateCaptor.capture(),
                repoNameCaptor.capture(), selectedBranchesCaptor.capture());

        final int lastIndex = importedResults.size() - 1;
        assertEquals(REPO_PULL_URL + lastIndex, pullURLCaptor.getValue());
        assertEquals(REPO_NAME + lastIndex, repoNameCaptor.getValue());
        assertEquals(0, selectedBranchesCaptor.getValue().size());
        assertEquals(now, observeFromDateCaptor.getValue());
    }
}
