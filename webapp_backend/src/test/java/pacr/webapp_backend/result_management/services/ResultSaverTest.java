package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.SystemEnvironment;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ResultSaverTest {

    private static final String UNIT = "unit";
    private static final String ERROR = "error";
    private static final String PROPERTY_NAME = "property";
    private static final String PROPERTY_NAME_TWO = "property2";
    private static final String BENCHMARK_NAME = "benchmark";
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String COMMIT_HASH = "hash";
    private static final String COMMIT_HASH_TWO = "hash2";
    private static final String NO_COMPARISON_COMMIT_HASH = null;
    private static final String NO_GLOBAL_ERROR = null;
    private static final double MEASUREMENT = 124d;
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
        resultSaver.saveResult(createSimpleResult(), NO_COMPARISON_COMMIT_HASH);

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
        resultSaver.saveResult(createSimpleResult(), NO_COMPARISON_COMMIT_HASH);

        SimpleBenchmarkingResult resultWithAddedBenchmark = createSimpleResult();
        resultWithAddedBenchmark.setCommitHash(COMMIT_HASH_TWO);

        SimpleBenchmark newBenchmark = createSimpleBenchmark();

        resultWithAddedBenchmark.addBenchmark(BENCHMARK_NAME_TWO, newBenchmark);

        resultSaver.saveResult(resultWithAddedBenchmark, NO_COMPARISON_COMMIT_HASH);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, benchmarkDB.count());
    }

    /**
     * Tests whether saveResult updates the correct number of properties.
     */
    @Test
    public void saveResult_withNewAndOldProperty_shouldOnlySaveNewProperty() {
        resultSaver.saveResult(createSimpleResult(), NO_COMPARISON_COMMIT_HASH);

        SimpleBenchmarkingResult resultWithAddedProperty = createSimpleResult();
        resultWithAddedProperty.setCommitHash(COMMIT_HASH_TWO);

        SimpleBenchmarkProperty newProperty = createSimpleProperty();
        resultWithAddedProperty.getBenchmark(BENCHMARK_NAME).addProperty(PROPERTY_NAME_TWO, newProperty);

        resultSaver.saveResult(resultWithAddedProperty, NO_COMPARISON_COMMIT_HASH);

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

    /**
     * Tests what happens when two results have same commit hash.
     */

    private SimpleBenchmarkingResult createSimpleResult() {
        HashMap<String, SimpleBenchmark> benchmarks = new HashMap<>();
        benchmarks.put(BENCHMARK_NAME, createSimpleBenchmark());

        SystemEnvironment systemEnvironment = new SystemEnvironment();

        return new SimpleBenchmarkingResult(COMMIT_HASH, systemEnvironment, benchmarks, NO_GLOBAL_ERROR);
    }

    private SimpleBenchmark createSimpleBenchmark() {
        HashMap<String, IBenchmarkProperty> properties = new HashMap<>();
        properties.put(PROPERTY_NAME, createSimpleProperty());
        return new SimpleBenchmark(properties);
    }

    private SimpleBenchmarkProperty createSimpleProperty() {
        LinkedList<Double> results = new LinkedList<>();
        results.add(MEASUREMENT);
        return new SimpleBenchmarkProperty(results, ResultInterpretation.MORE_IS_BETTER, UNIT, ERROR);
    }
}
