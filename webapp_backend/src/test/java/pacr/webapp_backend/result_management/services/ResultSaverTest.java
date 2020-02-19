package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ISystemEnvironment;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultSaverTest extends SpringBootTestWithoutShell {

    private static final String PROPERTY_NAME = SimpleBenchmark.PROPERTY_NAME;
    private static final String PROPERTY_NAME_TWO = "property2";
    private static final String BENCHMARK_NAME = SimpleBenchmarkingResult.BENCHMARK_NAME;
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String COMMIT_HASH = SimpleBenchmarkingResult.COMMIT_HASH;
    private static final String COMMIT_HASH_TWO = "hash2";
    private static final CommitResult NO_COMPARISON_RESULT = null;
    private static final String ERROR = "error";
    private static final double MEASUREMENT = SimpleBenchmarkProperty.MEASUREMENT;
    private static final int EXPECTED_NUM_OF_PROPERTIES = 1;
    private static final int EXPECTED_NUM_OF_PROPERTIES_AFTER_ADDING = 2;
    private static final int EXPECTED_NUM_OF_BENCHMARKS = 2;
    private static final int EXPECTED_NUM_OF_RESULTS = 1;
    private static final int EXPECTED_SINGLE_BENCHMARK = 1;

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
        resultSaver.saveResult(new SimpleBenchmarkingResult(), new SimpleCommit(), NO_COMPARISON_RESULT);

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
        resultSaver.saveResult(new SimpleBenchmarkingResult(), new SimpleCommit(), NO_COMPARISON_RESULT);

        SimpleBenchmarkingResult resultWithAddedBenchmark = new SimpleBenchmarkingResult();
        resultWithAddedBenchmark.setCommitHash(COMMIT_HASH_TWO);

        SimpleBenchmark newBenchmark = new SimpleBenchmark();

        resultWithAddedBenchmark.addBenchmark(BENCHMARK_NAME_TWO, newBenchmark);

        resultSaver.saveResult(resultWithAddedBenchmark, new SimpleCommit(), NO_COMPARISON_RESULT);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, benchmarkDB.count());
    }

    /**
     * Tests whether saveResult updates the correct number of properties.
     */
    @Test
    public void saveResult_withNewAndOldProperty_shouldOnlySaveNewProperty() {
        resultSaver.saveResult(new SimpleBenchmarkingResult(), new SimpleCommit(), NO_COMPARISON_RESULT);

        SimpleBenchmarkingResult resultWithAddedProperty = new SimpleBenchmarkingResult();
        resultWithAddedProperty.setCommitHash(COMMIT_HASH_TWO);

        SimpleBenchmarkProperty newProperty = new SimpleBenchmarkProperty();
        resultWithAddedProperty.getBenchmark(BENCHMARK_NAME).addProperty(PROPERTY_NAME_TWO, newProperty);

        resultSaver.saveResult(resultWithAddedProperty, new SimpleCommit(), NO_COMPARISON_RESULT);

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

    @Test
    void saveResult_withOldPropertyWithNewInterpretation_shouldUpdateDatabaseProperty() {
        resultSaver.saveResult(new SimpleBenchmarkingResult(), new SimpleCommit(), NO_COMPARISON_RESULT);

        SimpleBenchmarkingResult resultWithChangedProperty = new SimpleBenchmarkingResult();
        resultWithChangedProperty.setCommitHash(COMMIT_HASH_TWO);

        SimpleBenchmarkProperty property = resultWithChangedProperty.getBenchmark(BENCHMARK_NAME)
                .getProperty(PROPERTY_NAME);
        assertEquals(ResultInterpretation.LESS_IS_BETTER, property.getResultInterpretation());

        property.setResultInterpretation(ResultInterpretation.MORE_IS_BETTER);

        resultSaver.saveResult(resultWithChangedProperty, new SimpleCommit(), NO_COMPARISON_RESULT);

        Benchmark benchmark = null;
        for (Benchmark savedBenchmark : benchmarkDB.getAllBenchmarks()) {
            if (savedBenchmark.getOriginalName().equals(BENCHMARK_NAME)) {
                benchmark = savedBenchmark;
                break;
            }
        }

        assertNotNull(benchmark);
        assertEquals(ResultInterpretation.MORE_IS_BETTER,
                benchmark.getProperties().iterator().next().getInterpretation());
    }

    @Test
    void saveResult_benchmarkWithNoProperties_shouldSkipBenchmarkInCommitResult() {
        SimpleBenchmarkingResult result = new SimpleBenchmarkingResult();
        SimpleBenchmark benchmarkWithNoProperties = new SimpleBenchmark(new HashMap<>());
        result.addBenchmark(BENCHMARK_NAME_TWO, benchmarkWithNoProperties);

        resultSaver.saveResult(result, new SimpleCommit(), null);

        CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertEquals(EXPECTED_SINGLE_BENCHMARK, savedResult.getBenchmarks().size());
        assertNotNull(savedResult.getBenchmarks().get(SimpleBenchmarkingResult.BENCHMARK_NAME));
    }

    @Test
    void saveResult_resultWithNoBenchmarksAndNoError_shouldSetGlobalError() {
        ISystemEnvironment sysEnvMock = Mockito.mock(ISystemEnvironment.class);
        SimpleBenchmarkingResult emptyResult = new SimpleBenchmarkingResult(SimpleBenchmarkingResult.COMMIT_HASH,
                new SystemEnvironment(sysEnvMock), new HashMap<>(), null);

        resultSaver.saveResult(emptyResult, new SimpleCommit(), null);

        CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertTrue(savedResult.hasGlobalError());
    }

    @Test
    void saveResult_propertyWithNoResultsAndNoError_shouldSetLocalError() {
        SimpleBenchmarkProperty emptyProperty = new SimpleBenchmarkProperty(new HashSet<>(),
                ResultInterpretation.LESS_IS_BETTER, SimpleBenchmarkProperty.UNIT, null);

        HashMap<String, IBenchmarkProperty> propertyMap = new HashMap<>();
        propertyMap.put(PROPERTY_NAME_TWO, emptyProperty);

        SimpleBenchmark benchmarkOfProperty = new SimpleBenchmark(propertyMap);

        SimpleBenchmarkingResult result = new SimpleBenchmarkingResult();
        result.addBenchmark(BENCHMARK_NAME_TWO, benchmarkOfProperty);

        resultSaver.saveResult(result, new SimpleCommit(), null);

        CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertTrue(savedResult.getBenchmarks().get(BENCHMARK_NAME_TWO)
                .getBenchmarkProperties().get(PROPERTY_NAME_TWO).isError());
    }

    @Test
    void saveResult_newResultForCommitHashThatAlreadyHasOldResult_shouldOverwriteOldResult() {
        SimpleCommit commit = new SimpleCommit();
        SimpleBenchmarkingResult resultWithError = new SimpleBenchmarkingResult();
        resultWithError.setGlobalError(ERROR);

        resultSaver.saveResult(resultWithError, commit, NO_COMPARISON_RESULT);

        SimpleBenchmarkingResult resultWithoutError = new SimpleBenchmarkingResult();

        resultSaver.saveResult(resultWithoutError, commit, NO_COMPARISON_RESULT);

        CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);
        assertFalse(savedResult.hasGlobalError());

        Collection<String> hash = new LinkedList<>();
        hash.add(SimpleBenchmarkingResult.COMMIT_HASH);

        Collection<CommitResult> savedResults = resultDB.getResultsFromCommits(hash);

        assertEquals(EXPECTED_NUM_OF_RESULTS, savedResults.size());
        assertFalse(savedResults.iterator().next().hasGlobalError());

        CommitResult latestSavedResult = resultDB.getNewestResult(SimpleCommit.REPO_ID);
        assertFalse(latestSavedResult.hasGlobalError());
    }
}
