package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.git_tracking.GitBranch;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;
import pacr.webapp_backend.result_management.BenchmarkProperty;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.awt.Color;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OutputBuilderTest {

    public static final String MSG = "msg";
    public static final String REPO_NAME = "repo";
    public static final String BRANCH_NAME = "branch";
    public static final String URL = "url";
    public static final int REPO_ID = 1;
    public static final LocalDate NOW = LocalDate.now();
    public static final String BENCHMARK_NAME_TWO = "benchmark2";
    public static final String NO_GROUP_NAME = "";
    public static final String GROUP_NAME = "group";
    public static final int NO_GROUP_ID = -1;
    public static final int EXPECTED_NUM_OF_BENCHMARKS = 2;
    public static final int EXPECTED_NUM_OF_GROUPS_NO_GROUP = 1;
    public static final int EXPECTED_NUM_OF_GROUPS_ONE_GROUP = 2;
    public static final int EXPECTED_NUM_OF_BENCHMARKS_PER_GROUP = 1;
    public static final int FIRST = 0;
    public static final int SECOND = 1;

    private OutputBuilder outputBuilder;

    private CommitResult resultOne;
    private ICommit commitOne;
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
        BenchmarkResult benchmarkResultTwo = new BenchmarkResult(propertyResults, benchmarkTwo);

        Set<BenchmarkResult> benchmarkResults = new HashSet<>();
        benchmarkResults.add(benchmarkResult);
        benchmarkResults.add(benchmarkResultTwo);
        resultOne = new CommitResult(new SimpleBenchmarkingResult(), benchmarkResults, REPO_ID);

        commitOne = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW, new HashSet<>(),
                new GitRepository(false, new LinkedList<>(), URL, REPO_NAME, Color.BLACK, NOW),
                new GitBranch(BRANCH_NAME));
    }

    /**
     * Tests whether buildOutput properly builds one group for two benchmarks without a group.
     */
    @Test
    void buildOutput_twoBenchmarksWithNoGroup_shouldBuildOneOutputGroupWithTwoBenchmarks() {

        OutputBenchmarkingResult outputResult = outputBuilder.buildOutput(commitOne, resultOne);

        assertNotNull(outputResult);

        assertEquals(EXPECTED_NUM_OF_GROUPS_NO_GROUP, outputResult.getBenchmarkGroups().size());
        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, outputResult.getBenchmarkGroups().get(FIRST).getBenchmarks().size());
        assertEquals(NO_GROUP_NAME, outputResult.getBenchmarkGroups().get(FIRST).getName());
        assertEquals(NO_GROUP_ID, outputResult.getBenchmarkGroups().get(FIRST).getId());
    }

    /**
     * Tests whether buildOutput properly builds two groups for one benchmark with and one without a group.
     */
    @Test
    void buildOutput_twoBenchmarksWithAndWithoutGroup_shouldBuildTwoOutputGroups() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        benchmarkTwo.setGroup(group);

        OutputBenchmarkingResult outputResult = outputBuilder.buildOutput(commitOne, resultOne);

        assertEquals(EXPECTED_NUM_OF_GROUPS_ONE_GROUP, outputResult.getBenchmarkGroups().size());
        assertEquals(EXPECTED_NUM_OF_BENCHMARKS_PER_GROUP,
                outputResult.getBenchmarkGroups().get(FIRST).getBenchmarks().size());
        assertEquals(EXPECTED_NUM_OF_BENCHMARKS_PER_GROUP,
                outputResult.getBenchmarkGroups().get(SECOND).getBenchmarks().size());
    }
}
