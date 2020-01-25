package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.IBenchmarkingResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResultGetterTest {

    public static final String HASH = "hash";
    public static final String HASH_TWO = "hash2";
    public static final String BRANCH_NAME = "branch";
    public static final int REPO_ID = 1;
    public static final int BENCHMARK_ID = 5;
    public static final int BENCHMARK_ID_TWO = 10;
    public static final int EXPECTED_NUM_OF_RESULTS = 1;
    public static final int EXPECTED_NUM_OF_ALL_RESULTS = 1;
    public static final int EXPECTED_NUM_OF_NEW_RESULTS = 2;

    @Mock
    private IGetCommitAccess commitAccessMock;
    @Mock
    private IResultAccess resultAccessMock;
    @Mock
    private GitCommit commitMock;
    @Mock
    private CommitResult resultMock;
    @Mock
    private OutputBuilder outputBuilderMock;
    @Mock
    private OutputBenchmarkingResult outputResultMock;
    @Mock
    private DiagramOutputResult diagramOutputMock;

    private ResultGetter resultGetter;

    @Autowired
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

        OutputBenchmarkingResult outputResult = resultGetter.getCommitResult(HASH);

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
     * Tests whether getCommitResult properly throws exception if the result could not be found.
     */
    @Test
    void getCommitResult_noResultFound_shouldThrowNoSuchElement() {
        when(commitAccessMock.getCommit(HASH)).thenReturn(commitMock);
        when(resultAccessMock.getResultFromCommit(HASH)).thenReturn(null);

        Assertions.assertThrows(NoSuchElementException.class,  () -> {
            resultGetter.getCommitResult(HASH);
        });
    }

    /**
     * Tests whether getRepositoryResults returns all results.
     */
    @Test
    void getRepositoryResults_shouldReturnOutputResults() {
        when(commitAccessMock.getCommitsFromRepository(REPO_ID)).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                Collection<GitCommit> commits = new LinkedList<>();
                commits.add(commitMock);
                return commits;
            }
        });

        when(commitMock.getCommitHash()).thenReturn(HASH);

        Collection<CommitResult> results = new LinkedList<>();
        results.add(resultMock);

        when(resultAccessMock.getResultsFromCommits(anyCollection())).thenReturn(results);
        when(resultMock.getCommitHash()).thenReturn(HASH);
        when(outputBuilderMock.buildDiagramOutput(commitMock, resultMock)).thenReturn(diagramOutputMock);

        HashMap<String, DiagramOutputResult> outputs = resultGetter.getRepositoryResults(REPO_ID);

        assertEquals(EXPECTED_NUM_OF_RESULTS, outputs.size());
        assertEquals(diagramOutputMock, outputs.get(HASH));
    }

    /**
     * Tests whether getNewestResult returns the same object like the database access class.
     */
    @Test
    void getNewestResult_shouldReturnDatabaseAnswer() {
        when(resultAccessMock.getNewestResult(REPO_ID)).thenReturn(resultMock);

        IBenchmarkingResult testResult = resultGetter.getNewestResult(REPO_ID);

        assertEquals(resultMock, testResult);
    }

    @Test
    void exportAllBenchmarkingResults_shouldReturnDatabaseAnswer() {
        when(resultAccessMock.getAllResults()).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                List<CommitResult> results = new LinkedList<>();
                results.add(resultMock);
                return results;
            }
        });

        when(resultMock.getCommitHash()).thenReturn(HASH);

        List<? extends IBenchmarkingResult> testResults = resultGetter.exportAllBenchmarkingResults();

        assertEquals(EXPECTED_NUM_OF_ALL_RESULTS, testResults.size());
        assertEquals(HASH, testResults.get(0).getCommitHash());
    }

    /**
     * Tests whether getRepositoryResults returns empty collection if there are no results.
     */
    @Test
    void getRepositoryResults_noResults_shouldReturnEmptyCollection() {
        when(commitAccessMock.getCommitsFromRepository(REPO_ID)).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                return new LinkedList<GitCommit>();
            }
        });

        HashMap<String, DiagramOutputResult> outputs = resultGetter.getRepositoryResults(REPO_ID);

        assertEquals(0, outputs.size());
    }

    /**
     * Tests whether getBranchResults throws exception if the branch or repository does not exist.
     */
    @Test
    void getBranchResults_noBranch_shouldThrowNoSuchElementException() {
        when(commitAccessMock.getCommitsFromBranch(REPO_ID, BRANCH_NAME)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> {
            resultGetter.getBranchResults(REPO_ID, BRANCH_NAME);
        });
    }

    /**
     * Tests whether getBenchmarkResults only outputs the benchmark results that the caller is looking for and properly
     * removes other results.
     */
    @Test
    void getBenchmarkResults_extraBenchmark_shouldRemoveBenchmark() {
        when(commitAccessMock.getAllCommits()).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                Collection<GitCommit> commits = new LinkedList<>();
                commits.add(commitMock);
                return commits;
            }
        });

        when(commitMock.getCommitHash()).thenReturn(HASH);

        List<CommitResult> allResults = new LinkedList<>();
        allResults.add(resultMock);
        when(resultAccessMock.getResultsFromCommits(any())).thenReturn(allResults);

        BenchmarkResult benchmarkResultMock = Mockito.mock(BenchmarkResult.class);
        BenchmarkResult benchmarkResultMockTwo = Mockito.mock(BenchmarkResult.class);

        Benchmark benchmarkMock = Mockito.mock(Benchmark.class);
        Benchmark benchmarkTwoMock = Mockito.mock(Benchmark.class);

        List<BenchmarkResult> benchmarkResults = new LinkedList<>();
        benchmarkResults.add(benchmarkResultMock);
        benchmarkResults.add(benchmarkResultMockTwo);
        when(resultMock.getBenchmarksIterable()).thenReturn(benchmarkResults);

        when(benchmarkResultMock.getBenchmark()).thenReturn(benchmarkMock);
        when(benchmarkResultMockTwo.getBenchmark()).thenReturn(benchmarkTwoMock);

        when(benchmarkMock.getId()).thenReturn(BENCHMARK_ID);
        when(benchmarkTwoMock.getId()).thenReturn(BENCHMARK_ID_TWO);

        when(outputBuilderMock.buildDiagramOutput(any(), any())).thenReturn(diagramOutputMock);

        resultGetter.getBenchmarkResults(BENCHMARK_ID);

        verify(resultMock).removeBenchmarkResult(benchmarkResultMockTwo);
        verify(resultMock, never()).removeBenchmarkResult(benchmarkResultMock);
    }

    /**
     * Tests whether getBenchmarkResults (when called with repository id) only outputs the benchmark results that the
     * caller is looking for and properly removes other results.
     */
    @Test
    void getBenchmarkResults_callWithRepoId_shouldRemoveExtraBenchmark() {
        when(commitAccessMock.getCommitsFromRepository(REPO_ID)).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                Collection<GitCommit> commits = new LinkedList<>();
                commits.add(commitMock);
                return commits;
            }
        });

        when(commitMock.getCommitHash()).thenReturn(HASH);

        List<CommitResult> allResults = new LinkedList<>();
        allResults.add(resultMock);
        when(resultAccessMock.getResultsFromCommits(any())).thenReturn(allResults);

        BenchmarkResult benchmarkResultMock = Mockito.mock(BenchmarkResult.class);
        BenchmarkResult benchmarkResultMockTwo = Mockito.mock(BenchmarkResult.class);

        Benchmark benchmarkMock = Mockito.mock(Benchmark.class);
        Benchmark benchmarkTwoMock = Mockito.mock(Benchmark.class);

        List<BenchmarkResult> benchmarkResults = new LinkedList<>();
        benchmarkResults.add(benchmarkResultMock);
        benchmarkResults.add(benchmarkResultMockTwo);
        when(resultMock.getBenchmarksIterable()).thenReturn(benchmarkResults);

        when(benchmarkResultMock.getBenchmark()).thenReturn(benchmarkMock);
        when(benchmarkResultMockTwo.getBenchmark()).thenReturn(benchmarkTwoMock);

        when(benchmarkMock.getId()).thenReturn(BENCHMARK_ID);
        when(benchmarkTwoMock.getId()).thenReturn(BENCHMARK_ID_TWO);

        when(outputBuilderMock.buildDiagramOutput(any(), any())).thenReturn(diagramOutputMock);

        resultGetter.getBenchmarkResults(REPO_ID, BENCHMARK_ID);

        verify(resultMock).removeBenchmarkResult(benchmarkResultMockTwo);
        verify(resultMock, never()).removeBenchmarkResult(benchmarkResultMock);
    }

    /**
     * Tests whether getNewestResults properly builds the output objects and keeps their order from the database.
     */
    @Test
    void getNewestResults_shouldBuildOutputObjects() {
        List<CommitResult> results = new LinkedList<>();
        results.add(resultMock);
        CommitResult resultMockTwo = Mockito.mock(CommitResult.class);
        results.add(resultMockTwo);

        when(resultAccessMock.getNewestResults()).thenReturn(results);

        when(resultMock.getCommitHash()).thenReturn(HASH);
        when(resultMockTwo.getCommitHash()).thenReturn(HASH_TWO);

        when(commitAccessMock.getCommit(HASH)).thenReturn(commitMock);
        GitCommit commitTwo = Mockito.mock(GitCommit.class);

        when(commitAccessMock.getCommit(HASH_TWO)).thenReturn(commitTwo);

        when(outputBuilderMock.buildDetailOutput(commitMock, resultMock)).thenReturn(outputResultMock);
        OutputBenchmarkingResult outputResultMockTwo = Mockito.mock(OutputBenchmarkingResult.class);
        when(outputBuilderMock.buildDetailOutput(commitTwo, resultMockTwo)).thenReturn(outputResultMockTwo);

        List<OutputBenchmarkingResult> newestResults = resultGetter.getNewestResults();

        assertEquals(EXPECTED_NUM_OF_NEW_RESULTS, newestResults.size());
        assertEquals(outputResultMock, newestResults.get(0));
        assertEquals(outputResultMockTwo, newestResults.get(1));
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
}
