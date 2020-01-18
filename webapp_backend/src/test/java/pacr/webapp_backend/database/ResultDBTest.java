package pacr.webapp_backend.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.result_management.SystemEnvironment;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ResultDBTest {

    @Mock
    private SystemEnvironment systemEnvironmentMock;

    private static final String BENCHMARK_NAME = "benchmark";
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String GROUP_NAME = "group";
    private static final String COMMIT_HASH = "1234";
    private static final String COMMIT_HASH_TWO = "5678";
    private static final String COMMIT_HASH_THREE = "9101";

    private ResultDB resultDB;
    private BenchmarkDB benchmarkDB;
    private Benchmark benchmark;
    private Benchmark benchmarkTwo;

    @Autowired
    public ResultDBTest(ResultDB resultDB, BenchmarkDB benchmarkDB) {
        this.resultDB = resultDB;
        this.benchmarkDB = benchmarkDB;
        this.benchmark = new Benchmark(BENCHMARK_NAME);
        this.benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);
    }

    @BeforeEach
    public void setUp() {
        this.benchmarkDB.saveBenchmark(benchmark);
        this.benchmarkDB.saveBenchmark(benchmarkTwo);
    }

    @AfterEach
    public void cleanUp() {
        resultDB.deleteAll();
        benchmarkDB.deleteAll();
    }

    /**
     * Tests whether a result can be saved with saveResult and retrieved with getResultFromCommit
     */
    @Test
    public void saveResult_saveInDatabase_getResultShouldReturnResult() {
        CommitResult result = createNewCommitResult(COMMIT_HASH, benchmark);
        this.resultDB.saveResult(result);

        CommitResult savedResult = this.resultDB.getResultFromCommit(COMMIT_HASH);

        assertEquals(COMMIT_HASH, savedResult.getCommitHash());
        assertEquals(BENCHMARK_NAME, savedResult.getBenchmarksIterable().iterator().next().getName());
    }

    /**
     * Tests whether the benchmark metadata of a result changes if the benchmark is changed in the benchmarkDB.
     */
    @Test
    public void getResult_changedBenchmark_ShouldReturnChangedBenchmark() {
        CommitResult result = createNewCommitResult(COMMIT_HASH, benchmark);
        this.resultDB.saveResult(result);

        benchmark.setCustomName(BENCHMARK_NAME_TWO);
        this.benchmarkDB.saveBenchmark(benchmark);

        CommitResult savedResult = this.resultDB.getResultFromCommit(COMMIT_HASH);

        assertEquals(BENCHMARK_NAME_TWO,
                savedResult.getBenchmarksIterable().iterator().next().getBenchmark().getCustomName());
    }

    /**
     * Tests whether the proper results are returned if you enter multiple commit hashes.
     */
    @Test
    public void getResults_multipleHashesAsInput_ShouldReturnAllResults() {
        CommitResult resultOne = createNewCommitResult(COMMIT_HASH, benchmark);
        this.resultDB.saveResult(resultOne);
        CommitResult resultTwo = createNewCommitResult(COMMIT_HASH_TWO, benchmark);
        this.resultDB.saveResult(resultTwo);

        List<String> commitHashes = new LinkedList<>();
        commitHashes.add(COMMIT_HASH);
        commitHashes.add(COMMIT_HASH_TWO);

        Collection<CommitResult> savedResults = this.resultDB.getResultsFromCommits(commitHashes);

        assertEquals(2, savedResults.size());
    }

    /**
     * Tests whether a result can be deleted.
     */
    @Test
    public void deleteResult_resultSaved_shouldRemoveResult() {
        CommitResult result = createNewCommitResult(COMMIT_HASH, benchmark);
        this.resultDB.saveResult(result);

        this.resultDB.deleteResult(result);
        CommitResult deletedResult = this.resultDB.getResultFromCommit(COMMIT_HASH);

        assertNull(deletedResult);
    }

    /**
     * Tests whether multiple saved results are returned in the correct order by getNewestResult (even if a result is
     * deleted in between)
     */
    @Test
    public void getNewestResults_multipleResultsSavedOneDeleted_shouldReturnOrdered() {
        this.resultDB.saveResult(createNewCommitResult(COMMIT_HASH, benchmark));

        CommitResult resultToDelete = createNewCommitResult(COMMIT_HASH_TWO, benchmark);
        this.resultDB.saveResult(resultToDelete);

        this.resultDB.saveResult(createNewCommitResult(COMMIT_HASH_THREE, benchmark));

        this.resultDB.deleteResult(resultToDelete);

        this.resultDB.saveResult(createNewCommitResult(COMMIT_HASH_TWO, benchmark));

        LocalDateTime previousTime = LocalDateTime.now();

        List<CommitResult> orderedResults = this.resultDB.getNewestResults();

        for (CommitResult result : orderedResults) {
            assertTrue(result.getEntryDate().compareTo(previousTime) <= 0);
        }
    }

    private CommitResult createNewCommitResult(String commitHash, Benchmark benchmark) {
        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult();

        Set<BenchmarkPropertyResult> propertyResults = new HashSet<>();
        propertyResults.add(propertyResult);
        BenchmarkResult benchmarkResult = new BenchmarkResult(propertyResults, benchmark);

        Set<BenchmarkResult> benchmarkResults = new HashSet<>();
        benchmarkResults.add(benchmarkResult);

        return new CommitResult(commitHash, systemEnvironmentMock, benchmarkResults);
    }
}
