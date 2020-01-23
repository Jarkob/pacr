package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;
import pacr.webapp_backend.result_management.BenchmarkProperty;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.OutputBenchmark;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OutputBuilderTest {

    public static final String MSG = "msg";
    public static final String REPO_NAME = "repo";
    public static final String BRANCH_NAME = "branch";
    public static final String URL = "url";
    public static final int REPO_ID = 1;
    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final String BENCHMARK_NAME_TWO = "benchmark2";
    public static final String GROUP_NAME = "group";
    public static final int NO_GROUP_ID = -1;
    public static final int GROUP_ID = 0;
    public static final int EXPECTED_NUM_OF_BENCHMARKS = 2;
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final String COMPARISON_HASH = "adlifi";

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
        resultOne = new CommitResult(new SimpleBenchmarkingResult(), benchmarkResults, REPO_ID, COMPARISON_HASH);

        commitOne = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW,
                new GitRepository(false, new HashSet<>(), URL, REPO_NAME, Color.BLACK, NOW.toLocalDate()));
    }

    /**
     * Tests whether buildOutput properly builds one group for two benchmarks without a group.
     */
    @Test
    void buildOutput_twoBenchmarksWithNoGroup_shouldBuildOneOutputGroupWithTwoBenchmarks() {

        OutputBenchmarkingResult outputResult = outputBuilder.buildOutput(commitOne, resultOne);

        assertNotNull(outputResult);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, outputResult.getBenchmarksList().size());
        assertEquals(NO_GROUP_ID, outputResult.getBenchmarksList().get(FIRST).getGroupId());
        assertEquals(NO_GROUP_ID, outputResult.getBenchmarksList().get(SECOND).getGroupId());
    }

    /**
     * Tests whether buildOutput properly builds two groups for one benchmark with and one without a group.
     */
    @Test
    void buildOutput_twoBenchmarksWithAndWithoutGroup_shouldBuildTwoOutputGroups() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        benchmarkTwo.setGroup(group);

        OutputBenchmarkingResult outputResult = outputBuilder.buildOutput(commitOne, resultOne);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, outputResult.getBenchmarksList().size());

        boolean foundNoGroup = false;
        boolean foundGroup = false;

        for (OutputBenchmark outputBenchmark : outputResult.getBenchmarksList()) {
            if (outputBenchmark.getGroupId() == NO_GROUP_ID) {
                foundNoGroup = true;
            } else if (outputBenchmark.getGroupId() == GROUP_ID) {
                foundGroup = true;
            }
        }

        assertTrue(foundGroup && foundNoGroup);
    }
}
