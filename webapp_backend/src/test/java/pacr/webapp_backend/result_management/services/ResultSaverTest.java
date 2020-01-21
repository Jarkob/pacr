package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ResultSaverTest {

    private static final String PROPERTY_NAME = SimpleBenchmark.PROPERTY_NAME;
    private static final String PROPERTY_NAME_TWO = "property2";
    private static final String BENCHMARK_NAME = SimpleBenchmarkingResult.BENCHMARK_NAME;
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String COMMIT_HASH = SimpleBenchmarkingResult.COMMIT_HASH;
    private static final String COMMIT_HASH_TWO = "hash2";
    private static final String NO_COMPARISON_COMMIT_HASH = null;
    private static final double MEASUREMENT = SimpleBenchmarkProperty.MEASUREMENT;
    private static final int EXPECTED_NUM_OF_PROPERTIES = 1;
    private static final int EXPECTED_NUM_OF_PROPERTIES_AFTER_ADDING = 2;
    private static final int EXPECTED_NUM_OF_BENCHMARKS = 2;

    private ResultImportSaver resultSaver;
    private ResultDB resultDB;
    private BenchmarkDB benchmarkDB;

    @Autowired
    public ResultSaverTest(ResultImportSaver resultSaver, ResultDB resultDB, BenchmarkDB benchmarkDB) {
        this.resultSaver = resultSaver;
        this.resultDB = resultDB;
        this.benchmarkDB = benchmarkDB;
    }

    @AfterEach
    public void cleanUp() {
        resultDB.deleteAll();
        benchmarkDB.deleteAll();
    }

    /**
     * Tests whether a result saved with saveResult can be retrieved from the database and whether the associated
     * benchmark has also been saved.
     */
    @Test
    public void saveResult_shouldBeInDatabaseWithBenchmark() {
        resultSaver.saveResult(new SimpleBenchmarkingResult(), new SimpleCommit(), NO_COMPARISON_COMMIT_HASH);

        CommitResult savedResult = resultDB.getResultFromCommit(COMMIT_HASH);

        assertNotNull(savedResult);
        assertEquals(COMMIT_HASH, savedResult.getCommitHash());

        IBenchmark benchmarkResult = savedResult.getBenchmarks().get(BENCHMARK_NAME);
        assertNotNull(benchmarkResult);

        IBenchmarkProperty benchmarkProperty = benchmarkResult.getBenchmarkProperties().get(PROPERTY_NAME);
        assertNotNull(benchmarkProperty);
        assertEquals(MEASUREMENT, benchmarkProperty.getResults().iterator().next());

        Benchmark benchmark = benchmarkDB.getAllBenchmarks().iterator().next();
        assertNotNull(benchmark);
        assertEquals(BENCHMARK_NAME, benchmark.getOriginalName());
        assertEquals(EXPECTED_NUM_OF_PROPERTIES, benchmark.getProperties().size());
    }

    /**
     * Tests whether saveResult updates the correct number of benchmarks.
     */
    @Test
    public void saveResult_withNewAndOldBenchmark_shouldOnlySaveNewBenchmark() {
        resultSaver.saveResult(new SimpleBenchmarkingResult(), new SimpleCommit(), NO_COMPARISON_COMMIT_HASH);

        SimpleBenchmarkingResult resultWithAddedBenchmark = new SimpleBenchmarkingResult();
        resultWithAddedBenchmark.setCommitHash(COMMIT_HASH_TWO);

        SimpleBenchmark newBenchmark = new SimpleBenchmark();

        resultWithAddedBenchmark.addBenchmark(BENCHMARK_NAME_TWO, newBenchmark);

        resultSaver.saveResult(resultWithAddedBenchmark, new SimpleCommit(), NO_COMPARISON_COMMIT_HASH);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, benchmarkDB.count());
    }

    /**
     * Tests whether saveResult updates the correct number of properties.
     */
    @Test
    public void saveResult_withNewAndOldProperty_shouldOnlySaveNewProperty() {
        resultSaver.saveResult(new SimpleBenchmarkingResult(), new SimpleCommit(), NO_COMPARISON_COMMIT_HASH);

        SimpleBenchmarkingResult resultWithAddedProperty = new SimpleBenchmarkingResult();
        resultWithAddedProperty.setCommitHash(COMMIT_HASH_TWO);

        SimpleBenchmarkProperty newProperty = new SimpleBenchmarkProperty();
        resultWithAddedProperty.getBenchmark(BENCHMARK_NAME).addProperty(PROPERTY_NAME_TWO, newProperty);

        resultSaver.saveResult(resultWithAddedProperty, new SimpleCommit(), NO_COMPARISON_COMMIT_HASH);

        Benchmark benchmark = null;
        for (Benchmark savedBenchmark : benchmarkDB.getAllBenchmarks()) {
            if (savedBenchmark.getOriginalName().equals(BENCHMARK_NAME)) {
                benchmark = savedBenchmark;
                break;
            }
        }

        assertNotNull(benchmark);
        assertEquals(EXPECTED_NUM_OF_PROPERTIES_AFTER_ADDING, benchmark.getProperties().size());
    }
}
