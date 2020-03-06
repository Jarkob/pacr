package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.IObserver;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pacr.webapp_backend.result_management.services.OutputBuilderTest.PROPERTY_NAME_TWO;
import static pacr.webapp_backend.result_management.services.SimpleBenchmark.PROPERTY_NAME;
import static pacr.webapp_backend.result_management.services.SimpleBenchmarkProperty.UNIT;
import static pacr.webapp_backend.result_management.services.SimpleBenchmarkingResult.BENCHMARK_NAME;

public class ResultGetterTest {

    public static final String HASH = "hash";
    public static final String HASH_TWO = "hash2";
    public static final String BRANCH_NAME = "branch";
    public static final int REPO_ID = 1;
    public static final int BENCHMARK_ID = 5;
    public static final int BENCHMARK_ID_TWO = 10;
    public static final int EXPECTED_SINGLE_RESULT = 1;
    public static final int EXPECTED_NUM_OF_ALL_RESULTS = 1;
    public static final int EXPECTED_NUM_OF_NEW_RESULTS = 2;
    public static final int PAGE_NUM = 0;
    public static final int PAGE_SIZE = 200;

    @Mock
    private final IGetCommitAccess commitAccessMock;
    @Mock
    private final IResultAccess resultAccessMock;
    @Mock
    private final GitCommit commitMock;
    @Mock
    private final CommitResult resultMock;
    @Mock
    private final OutputBuilder outputBuilderMock;
    @Mock
    private final OutputBenchmarkingResult outputResultMock;
    @Mock
    private final DiagramOutputResult diagramOutputMock;

    private final ResultGetter resultGetter;

    public ResultGetterTest() {
        commitAccessMock = Mockito.mock(IGetCommitAccess.class);
        resultAccessMock = Mockito.mock(IResultAccess.class);
        commitMock = Mockito.mock(GitCommit.class);
        resultMock = Mockito.mock(CommitResult.class);
        outputBuilderMock = Mockito.mock(OutputBuilder.class);
        outputResultMock = Mockito.mock(OutputBenchmarkingResult.class);
        diagramOutputMock = Mockito.mock(DiagramOutputResult.class);
        this.resultGetter = new ResultGetter(commitAccessMock, resultAccessMock, outputBuilderMock);
    }

    /**
     * Tests whether getCommitResult returns the output result if everything is in order (commit and result are saved
     * in database).
     */
    @Test
    void getCommitResult_shouldBuildOutputObject() {
        when(commitAccessMock.getCommit(HASH)).thenReturn(commitMock);
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(resultMock);
        when(outputBuilderMock.buildDetailOutput(commitMock, resultMock)).thenReturn(outputResultMock);

        final OutputBenchmarkingResult outputResult = resultGetter.getCommitResult(HASH);

        assertEquals(outputResultMock, outputResult);
    }

    /**
     * Tests whether getCommitResult properly throws exception if the commit could not be found.
     */
    @Test
    void getCommitResult_noCommitFound_shouldThrowNoSuchElement() {
        when(commitAccessMock.getCommit(HASH)).thenReturn(null);
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(resultMock);

        Assertions.assertThrows(NoSuchElementException.class,  () -> {
            resultGetter.getCommitResult(HASH);
        });
    }

    /**
     * Tests whether getCommitResult returns a result without result data if no such data could be found.
     */
    @Test
    void getCommitResult_noResultFound_shouldReturnCommitData() {
        when(commitAccessMock.getCommit(HASH)).thenReturn(commitMock);
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(null);
        when(outputBuilderMock.buildDetailOutput(commitMock)).thenReturn(outputResultMock);

        final OutputBenchmarkingResult output = resultGetter.getCommitResult(HASH);

        assertEquals(outputResultMock, output);
    }

    @Test
    void getCommitResult_parameterIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> resultGetter.getCommitResult(null));
    }

    @Test
    void getFullRepositoryResults_shouldReturnDatabaseAnswer() {
        final PageRequest pageable = PageRequest.of(PAGE_NUM, PAGE_SIZE);

        final List<CommitResult> results = new LinkedList<>();
        results.add(resultMock);

        when(resultAccessMock.getFullRepositoryResults(REPO_ID, pageable)).thenReturn(new PageImpl<>(results));

        when(resultMock.getCommitHash()).thenReturn(HASH);
        when(commitAccessMock.getCommit(HASH)).thenReturn(commitMock);
        when(outputBuilderMock.buildDetailOutput(commitMock, resultMock)).thenReturn(outputResultMock);

        final Page<OutputBenchmarkingResult> output = resultGetter.getFullRepositoryResults(REPO_ID, pageable);

        assertEquals(EXPECTED_SINGLE_RESULT, output.getContent().size());
        assertEquals(outputResultMock, output.getContent().get(0));
    }

    /**
     * Tests whether getNewestResult returns the same object like the database access class.
     */
    @Test
    void getNewestResult_shouldReturnDatabaseAnswer() {
        when(resultAccessMock.getNewestResult(REPO_ID)).thenReturn(resultMock);

        final IBenchmarkingResult testResult = resultGetter.getNewestResult(REPO_ID);

        assertEquals(resultMock, testResult);
    }

    @Test
    void exportAllBenchmarkingResults_shouldReturnDatabaseAnswer() {
        when(resultAccessMock.getAllResults()).thenAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) {
                final List<CommitResult> results = new LinkedList<>();
                results.add(resultMock);
                return results;
            }
        });

        when(resultMock.getCommitHash()).thenReturn(HASH);

        final List<? extends IBenchmarkingResult> testResults = resultGetter.exportAllBenchmarkingResults();

        assertEquals(EXPECTED_NUM_OF_ALL_RESULTS, testResults.size());
        assertEquals(HASH, testResults.get(0).getCommitHash());
    }

    /**
     * Tests whether getBenchmarkResults (when called with repository id) only outputs the benchmark results that the
     * caller is looking for and properly removes other results.
     */
    @Test
    void getBenchmarkResults_callWithRepoId_shouldRemoveExtraBenchmark() {
        when(commitAccessMock.getCommitsFromRepository(REPO_ID)).thenAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) {
                final Collection<GitCommit> commits = new LinkedList<>();
                commits.add(commitMock);
                return commits;
            }
        });

        when(commitMock.getCommitHash()).thenReturn(HASH);

        final List<CommitResult> allResults = new LinkedList<>();
        allResults.add(resultMock);
        when(resultAccessMock.getResultsFromCommits(any())).thenReturn(allResults);

        when(resultMock.getCommitHash()).thenReturn(HASH);

        final BenchmarkResult benchmarkResultMock = Mockito.mock(BenchmarkResult.class);
        final BenchmarkResult benchmarkResultMockTwo = Mockito.mock(BenchmarkResult.class);

        final Benchmark benchmarkMock = Mockito.mock(Benchmark.class);
        final Benchmark benchmarkTwoMock = Mockito.mock(Benchmark.class);

        final HashSet<BenchmarkResult> benchmarkResults = new HashSet<>();
        benchmarkResults.add(benchmarkResultMock);
        benchmarkResults.add(benchmarkResultMockTwo);
        when(resultMock.getBenchmarkResults()).thenReturn(benchmarkResults);

        when(benchmarkResultMock.getBenchmark()).thenReturn(benchmarkMock);
        when(benchmarkResultMockTwo.getBenchmark()).thenReturn(benchmarkTwoMock);

        when(benchmarkMock.getId()).thenReturn(BENCHMARK_ID);
        when(benchmarkTwoMock.getId()).thenReturn(BENCHMARK_ID_TWO);

        when(outputBuilderMock.buildDiagramOutput(any(), any(), anyInt())).thenReturn(diagramOutputMock);

        final Map<String, DiagramOutputResult> results = resultGetter.getBenchmarkResults(REPO_ID, BENCHMARK_ID);

        assertEquals(EXPECTED_SINGLE_RESULT, results.size());
        assertEquals(diagramOutputMock, results.get(HASH));
    }

    /**
     * Tests whether getNewestResults properly builds the output objects and keeps their order from the database.
     */
    @Test
    void getNewestResults_shouldBuildOutputObjects() {
        final List<CommitResult> results = new LinkedList<>();
        results.add(resultMock);
        final CommitResult resultMockTwo = Mockito.mock(CommitResult.class);
        results.add(resultMockTwo);

        Page<CommitResult> page = new PageImpl<>(results);

        PageRequest pageRequest = PageRequest.of(PAGE_NUM, PAGE_SIZE);

        when(resultAccessMock.getNewestResults(pageRequest)).thenReturn(page);

        when(resultMock.getCommitHash()).thenReturn(HASH);
        when(resultMockTwo.getCommitHash()).thenReturn(HASH_TWO);

        when(commitAccessMock.getCommit(HASH)).thenReturn(commitMock);
        final GitCommit commitTwo = Mockito.mock(GitCommit.class);
        when(commitAccessMock.getCommit(HASH_TWO)).thenReturn(commitTwo);

        when(resultMock.getEntryDate()).thenReturn(LocalDateTime.now());
        when(commitMock.getCommitDate()).thenReturn(LocalDateTime.now());
        when(commitMock.getAuthorDate()).thenReturn(LocalDateTime.now());
        when(resultMockTwo.getEntryDate()).thenReturn(LocalDateTime.now());
        when(commitTwo.getCommitDate()).thenReturn(LocalDateTime.now());
        when(commitTwo.getAuthorDate()).thenReturn(LocalDateTime.now());


        Page<CommitHistoryItem> newestResults = resultGetter.getNewestResults(pageRequest);

        assertEquals(EXPECTED_NUM_OF_NEW_RESULTS, newestResults.getContent().size());
    }

    /**
     * Tests whether getBenchmarkResultsSubset properly builds output objects.
     */
    @Test
    void getBenchmarkResultsSubset_shouldBuildOutputObjects() {
        List<GitCommit> commits = new LinkedList<>();
        commits.add(commitMock);

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(1);

        when(commitAccessMock.getCommitsFromBranchTimeFrame(anyInt(), anyString(), any(), any())).thenAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) {
                return commits;
            }
        });

        when(commitMock.getCommitHash()).thenReturn(HASH);

        final Collection<CommitResult> results = new LinkedList<>();
        results.add(resultMock);

        when(resultAccessMock.getResultsFromCommits(anyCollection())).thenReturn(results);
        when(resultMock.getCommitHash()).thenReturn(HASH);
        when(outputBuilderMock.buildDiagramOutput(commitMock, resultMock, BENCHMARK_ID)).thenReturn(diagramOutputMock);

        Map<String, DiagramOutputResult> outputs = resultGetter.getBenchmarkResultsSubset(BENCHMARK_ID, REPO_ID,
                BRANCH_NAME, startTime, endTime);

        assertEquals(EXPECTED_SINGLE_RESULT, outputs.size());
        assertEquals(diagramOutputMock, outputs.get(HASH));
    }

    @Test
    void getMeasurementsOfPropertyForCommit_commitResultExists_shouldReturnMeasurements() {
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(resultMock);

        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult(new SimpleBenchmarkProperty(), property);

        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        BenchmarkResult benchmarkResult = new BenchmarkResult(benchmark);
        benchmarkResult.addPropertyResult(propertyResult);

        Set<BenchmarkResult> benchmarkResults = new HashSet<>();
        benchmarkResults.add(benchmarkResult);

        when(resultMock.getBenchmarkResults()).thenReturn(benchmarkResults);

        List<Double> measurements = resultGetter
                .getMeasurementsOfPropertyForCommit(HASH, benchmark.getId(), PROPERTY_NAME);

        assertEquals(propertyResult.getResults(), measurements);
    }

    @Test
    void getMeasurementsOfPropertyForCommit_inputIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
                resultGetter.getMeasurementsOfPropertyForCommit(null, 0, null));
    }

    @Test
    void getMeasurementsOfPropertyForCommit_commitResultDoesntExist_shouldReturnEmptyList() {
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(null);
        assertTrue(resultGetter.getMeasurementsOfPropertyForCommit(HASH, BENCHMARK_ID, PROPERTY_NAME).isEmpty());
    }

    @Test
    void getMeasurementsOfPropertyForCommit_commitResultExistsButNotProperty_shouldReturnEmptyList() {
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(resultMock);

        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult(new SimpleBenchmarkProperty(), property);

        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        BenchmarkResult benchmarkResult = new BenchmarkResult(benchmark);
        benchmarkResult.addPropertyResult(propertyResult);

        Set<BenchmarkResult> benchmarkResults = new HashSet<>();
        benchmarkResults.add(benchmarkResult);

        when(resultMock.getBenchmarkResults()).thenReturn(benchmarkResults);

        assertTrue(resultGetter.getMeasurementsOfPropertyForCommit(HASH, benchmark.getId(), PROPERTY_NAME_TWO).isEmpty());
    }

    /**
     * Tests whether isCommitBenchmarked returns false if the commit does not have a result.
     */
    @Test
    void isCommitBenchmarked_notBenchmarked_shouldReturnFalse() {
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(null);
        assertFalse(resultGetter.isCommitBenchmarked(HASH));
    }

    /**
     * Tests whether isCommitBenchmarked returns true if the commit has a result.
     */
    @Test
    void isCommitBenchmarked_isBenchmarked_shouldReturnTrue() {
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(resultMock);
        assertTrue(resultGetter.isCommitBenchmarked(HASH));
    }

    @Test
    void isCommitBenchmarked_parameterIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> resultGetter.isCommitBenchmarked(null));
    }

    /**
     * Tests whether a subscribed observer receives updates.
     */
    @Test
    void subscribe_updateObservers_shouldUpdateSubscriber() {
        final IObserver observer = Mockito.mock(IObserver.class);

        resultGetter.subscribe(observer);
        resultGetter.updateAll();

        verify(observer).update();

        resultGetter.unsubscribe(observer);
    }

    /**
     * Tests whether an unsubscribed does not receive updates.
     */
    @Test
    void unsubscribe_updateObservers_shouldNotUpdateUnsubscribed() {
        final IObserver observer = Mockito.mock(IObserver.class);

        resultGetter.subscribe(observer);
        resultGetter.unsubscribe(observer);
        resultGetter.updateAll();

        verify(observer, never()).update();
    }
}
