package pacr.webapp_backend.database;

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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ResultDBTest {

    @Mock
    private SystemEnvironment systemEnvironmentMock;

    private static final String BENCHMARK_NAME = "benchmark";
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String COMMIT_HASH = "1234";
    private static final String COMMIT_HASH_TWO = "5678";

    private ResultDB resultDB;
    private BenchmarkDB benchmarkDB;

    @Autowired
    public ResultDBTest(ResultDB resultDB, BenchmarkDB benchmarkDB) {
        this.resultDB = resultDB;
        this.benchmarkDB = benchmarkDB;
    }

    @BeforeEach
    public void setUp() {
        resultDB.deleteAll();
        benchmarkDB.deleteAll();
    }

    /**
     * Tests whether a result can be saved with saveResult and retrieved with getResultFromCommit
     */
    @Test
    public void saveResult_saveInDatabase_getResultShouldReturnResult() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        this.benchmarkDB.saveBenchmark(benchmark);

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
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        this.benchmarkDB.saveBenchmark(benchmark);

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
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        this.benchmarkDB.saveBenchmark(benchmark);

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

    private CommitResult createNewCommitResult(String commitHash, Benchmark benchmark) {
        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult();

        List<BenchmarkPropertyResult> propertyResults = new LinkedList<>();
        propertyResults.add(propertyResult);
        BenchmarkResult benchmarkResult = new BenchmarkResult(propertyResults, benchmark);

        List<BenchmarkResult> benchmarkResults = new LinkedList<>();
        benchmarkResults.add(benchmarkResult);

        return new CommitResult(commitHash, systemEnvironmentMock, benchmarkResults);
    }
}
