package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.git_tracking.GitBranch;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkProperty;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.awt.Color;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OutputBuilderTest {

    public static final String HASH_TWO = "hash2";
    public static final String MSG = "msg";
    public static final String REPO_NAME = "repo";
    public static final String BRANCH_NAME = "branch";
    public static final String URL = "url";
    public static final int REPO_ID = 1;
    public static final LocalDate NOW = LocalDate.now();
    public static final String BENCHMARK_NAME_TWO = "benchmark2";
    public static final int EXPECTED_NUM_OF_OUTPUTS = 2;
    public static final int EXPECTED_NUM_OF_GROUPS = 1;

    private OutputBuilder outputBuilder;

    private CommitResult resultOne;
    private CommitResult resultTwo;
    private ICommit commitOne;
    private ICommit commitTwo;
    private Benchmark benchmark;
    private Benchmark benchmarkTwo;
    private BenchmarkProperty property;

    public OutputBuilderTest() {
        this.outputBuilder = new OutputBuilder();
    }

    @BeforeEach
    public void setUp() {
        benchmark = new Benchmark(SimpleBenchmarkingResult.BENCHMARK_NAME);
        benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);

        property = new BenchmarkProperty(SimpleBenchmark.PROPERTY_NAME, SimpleBenchmarkProperty.UNIT,
                ResultInterpretation.LESS_IS_BETTER, benchmark);

        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult(new SimpleBenchmarkProperty(), property);

        Set<BenchmarkPropertyResult> propertyResults = new HashSet<>();
        propertyResults.add(propertyResult);
        BenchmarkResult benchmarkResult = new BenchmarkResult(propertyResults, benchmark);

        Set<BenchmarkResult> benchmarkResults = new HashSet<>();
        benchmarkResults.add(benchmarkResult);
        resultOne = new CommitResult(new SimpleBenchmarkingResult(), benchmarkResults, REPO_ID);

        BenchmarkResult benchmarkResultTwo = new BenchmarkResult(propertyResults, benchmarkTwo);
        Set<BenchmarkResult> benchmarkResultsTwo = new HashSet<>();
        benchmarkResultsTwo.add(benchmarkResultTwo);

        SimpleBenchmarkingResult inputResultTwo = new SimpleBenchmarkingResult();
        inputResultTwo.setCommitHash(HASH_TWO);
        resultTwo = new CommitResult(inputResultTwo, benchmarkResultsTwo, REPO_ID);

        commitOne = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW, new HashSet<>(),
                new GitRepository(false, new LinkedList<>(), URL, REPO_NAME, Color.BLACK, NOW),
                new GitBranch(BRANCH_NAME));

        commitTwo = new GitCommit(HASH_TWO, MSG, NOW, NOW, new HashSet<>(),
                new GitRepository(false, new LinkedList<>(), URL, REPO_NAME, Color.BLACK, NOW),
                new GitBranch(BRANCH_NAME));
    }

    /**
     * Tests whether buildOutput properly builds two output objects.
     */
    @Test
    void buildOutput_shouldCreateTwoOutputObjects() {

        OutputBenchmarkingResult outputResult = outputBuilder.buildOutput(commitOne, resultOne);

        assertNotNull(outputResult);

        assertEquals(EXPECTED_NUM_OF_GROUPS, outputResult.getBenchmarkGroups().size());
    }
}
