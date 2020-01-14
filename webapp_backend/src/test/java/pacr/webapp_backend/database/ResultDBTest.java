package pacr.webapp_backend.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        Benchmark benchmark = new Benchmark("benchmark");
        this.benchmarkDB.saveBenchmark(benchmark);

        CommitResult result = createNewCommitResult("1234", "os", benchmark);
        this.resultDB.saveResult(result);

        CommitResult savedResult = this.resultDB.getResultFromCommit("1234");
        assertEquals("1234", savedResult.getCommitHash());
        assertEquals("os", savedResult.getSystemEnvironment().getOS());
        assertEquals("benchmark", savedResult.getBenchmarksIterable().iterator().next().getName());
    }

    /**
     * Tests whether the benchmark metadata of a result changes if the benchmark is changed in the benchmarkDB.
     */
    @Test
    public void getResult_changedBenchmark_ShouldReturnChangedBenchmark() {
        Benchmark benchmark = new Benchmark("benchmark");
        this.benchmarkDB.saveBenchmark(benchmark);

        CommitResult result = createNewCommitResult("1234", "os", benchmark);
        this.resultDB.saveResult(result);

        benchmark.setCustomName("newName");
        this.benchmarkDB.saveBenchmark(benchmark);

        CommitResult savedResult = this.resultDB.getResultFromCommit("1234");
        assertEquals("newName", savedResult.getBenchmarksIterable().iterator().next().getBenchmark().getCustomName());
    }

    /**
     * Tests whether the proper results are returned if you enter multiple commit hashes.
     */
    @Test
    public void getResults_multipleHashesAsInput_ShouldReturnAllResults() {
        Benchmark benchmark = new Benchmark("benchmark");
        this.benchmarkDB.saveBenchmark(benchmark);

        CommitResult resultOne = createNewCommitResult("1234", "os", benchmark);
        this.resultDB.saveResult(resultOne);
        CommitResult resultTwo = createNewCommitResult("5678", "os", benchmark);
        this.resultDB.saveResult(resultTwo);

        List<String> commitHashes = new LinkedList<>();
        commitHashes.add("1234");
        commitHashes.add("5678");

        Collection<CommitResult> savedResults = this.resultDB.getResultsFromCommits(commitHashes);
        assertEquals(2, savedResults.size());
    }

    private CommitResult createNewCommitResult(String commitHash, String os, Benchmark benchmark) {
        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult();

        List<BenchmarkPropertyResult> propertyResults = new LinkedList<>();
        propertyResults.add(propertyResult);
        BenchmarkResult benchmarkResult = new BenchmarkResult(propertyResults, benchmark);

        SystemEnvironment systemEnvironment = new SystemEnvironment(os, "kernel", 1, 4);

        List<BenchmarkResult> benchmarkResults = new LinkedList<>();
        benchmarkResults.add(benchmarkResult);
        return new CommitResult(commitHash, systemEnvironment, benchmarkResults);
    }
}
