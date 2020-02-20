package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class OutputBuilderTest {

    public static final String HASH_TWO = "hash2";
    public static final String MSG = "msg";
    public static final int REPO_ID = 1;
    public static final int BENCHMARK_ID_ONE = 1;
    public static final int BENCHMARK_ID_TWO = 2;
    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final String BENCHMARK_NAME_TWO = "benchmark2";
    public static final String GROUP_NAME = "group";
    public static final int NO_GROUP_ID = -1;
    public static final int GROUP_ID = 0;
    public static final int EXPECTED_NUM_OF_BENCHMARKS = 2;
    public static final int EXPECTED_NUM_OF_PROPERTIES = 1;
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final String COMPARISON_HASH = "adlifi";
    public static final String PROPERTY_NAME_TWO = "pro2";
    public static final String ERROR = "there was an error";

    private OutputBuilder outputBuilder;

    private CommitResult resultOne;
    private ICommit commitOne;
    private ICommit commitDifferentHash;
    private Benchmark benchmark;
    private Benchmark benchmarkTwo;
    private BenchmarkProperty property;
    private BenchmarkProperty propertyTwo;

    public OutputBuilderTest() {
        this.outputBuilder = new OutputBuilder();
    }

    @BeforeEach
    public void setUp() {
        //benchmark = new Benchmark(SimpleBenchmarkingResult.BENCHMARK_NAME);
        //benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);

        benchmark = Mockito.mock(Benchmark.class);
        benchmarkTwo = Mockito.mock(Benchmark.class);
        when(benchmark.getOriginalName()).thenReturn(SimpleBenchmarkingResult.BENCHMARK_NAME);
        when(benchmarkTwo.getOriginalName()).thenReturn(BENCHMARK_NAME_TWO);
        when(benchmark.getId()).thenReturn(BENCHMARK_ID_ONE);
        when(benchmarkTwo.getId()).thenReturn(BENCHMARK_ID_TWO);

        property = new BenchmarkProperty(SimpleBenchmark.PROPERTY_NAME, SimpleBenchmarkProperty.UNIT,
                ResultInterpretation.LESS_IS_BETTER);

        propertyTwo = new BenchmarkProperty(PROPERTY_NAME_TWO, SimpleBenchmarkProperty.UNIT,
                ResultInterpretation.LESS_IS_BETTER);

        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult(new SimpleBenchmarkProperty(), property);

        SimpleBenchmarkProperty iPropResult = new SimpleBenchmarkProperty();
        iPropResult.setError(ERROR);
        BenchmarkPropertyResult propertyResultTwo = new BenchmarkPropertyResult(iPropResult, propertyTwo);

        BenchmarkResult benchmarkResult = new BenchmarkResult(benchmark);
        benchmarkResult.addPropertyResult(propertyResult);
        BenchmarkResult benchmarkResultTwo = new BenchmarkResult(benchmarkTwo);
        benchmarkResultTwo.addPropertyResult(propertyResultTwo);

        resultOne = new CommitResult(new SimpleBenchmarkingResult(), REPO_ID, LocalDateTime.now(), COMPARISON_HASH);
        resultOne.addBenchmarkResult(benchmarkResult);
        resultOne.addBenchmarkResult(benchmarkResultTwo);

        commitOne = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW,
                new GitRepository());

        commitDifferentHash = new GitCommit(HASH_TWO, MSG, NOW, NOW, new GitRepository());
    }

    /**
     * Tests whether buildOutput properly builds one group for two benchmarks without a group.
     */
    @Test
    void buildDetailOutput_twoBenchmarksWithNoGroup_shouldBuildOneOutputGroupWithTwoBenchmarks() {

        OutputBenchmarkingResult outputResult = outputBuilder.buildDetailOutput(commitOne, resultOne);

        assertNotNull(outputResult);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, outputResult.getBenchmarksList().size());
        assertEquals(NO_GROUP_ID, outputResult.getBenchmarksList().get(FIRST).getGroupId());
        assertEquals(NO_GROUP_ID, outputResult.getBenchmarksList().get(SECOND).getGroupId());
    }

    @Test
    void buildDetailOutput_differentHashes_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> outputBuilder.buildDetailOutput(commitDifferentHash, resultOne));
    }

    @Test
    void buildDetailOutput_onlyCommit_shouldReturnObjectWithNoResultAndNoError() {
        OutputBenchmarkingResult output = outputBuilder.buildDetailOutput(commitOne);

        assertEquals(commitOne.getCommitHash(), output.getCommitHash());
        assertTrue(output.getBenchmarksList().isEmpty());
        assertFalse(output.isGlobalError());
    }

    @Test
    void buildDiagramOutput_shouldCopyData() {
        DiagramOutputResult output = outputBuilder.buildDiagramOutput(commitOne, resultOne, benchmark.getId());

        assertEquals(EXPECTED_NUM_OF_PROPERTIES , output.getResult().size());

        assertEquals(SimpleBenchmarkProperty.MEASUREMENT, output.getResult().get(SimpleBenchmark.PROPERTY_NAME).getResult());
        assertNull(output.getResult().get(SimpleBenchmark.PROPERTY_NAME).getErrorMessage());
    }

    @Test
    void buildDiagramOutput_onlyCommit_shouldReturnObjectWithNoResult() {
        DiagramOutputResult output = outputBuilder.buildDiagramOutput(commitOne);

        assertEquals(commitOne.getCommitHash(), output.getCommitHash());
        assertNull(output.getResult());
        assertNull(output.getGlobalError());
    }
}
