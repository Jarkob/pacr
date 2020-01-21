package pacr.webapp_backend.result_management.services;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.OutputBenchmark;
import pacr.webapp_backend.result_management.OutputBenchmarkGroup;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
import pacr.webapp_backend.result_management.OutputPropertyResult;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Creates a structured OutputBenchmarkingResult.
 */
@Component
public class OutputBuilder {

    /**
     * Creates OutputBenchmarkingResult based on a commit and its result. The result must belong to the commit (which
     * means they must refer to the same commit hash).
     * @param commit the commit. Cannot be null.
     * @param result the result. Cannot be null.
     * @return an OutputBenchmarkingResult copied from the commit and result.
     */
    OutputBenchmarkingResult buildOutput(@NotNull ICommit commit, @NotNull CommitResult result) {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(result);

        if (!commit.getCommitHash().equals(result.getCommitHash())) {
            throw new IllegalArgumentException("commit and result must have same commit hash");
        }

        List<OutputBenchmarkGroup> outputGroups = buildOutputGroups(result);

        return new OutputBenchmarkingResult(commit, result,
                outputGroups.toArray(new OutputBenchmarkGroup[0]));
    }

    private List<OutputBenchmarkGroup> buildOutputGroups(CommitResult result) {
        Iterable<BenchmarkResult> benchmarkResults = result.getBenchmarksIterable();

        HashMap<BenchmarkGroup, List<OutputBenchmark>> outputGroupsMap = new HashMap<>();
        List<OutputBenchmark> outputBenchmarksWithoutGroup = new LinkedList<>();

        for (BenchmarkResult benchmarkResult : benchmarkResults) {
            OutputBenchmark outputBenchmark = buildOutputBenchmark(benchmarkResult);

            BenchmarkGroup group = benchmarkResult.getBenchmark().getGroup();

            if (group != null) {
                outputGroupsMap.putIfAbsent(group, new LinkedList<>());
                outputGroupsMap.get(group).add(outputBenchmark);
            } else {
                outputBenchmarksWithoutGroup.add(outputBenchmark);
            }
        }

        List<OutputBenchmarkGroup> outputGroups = new LinkedList<>();

        OutputBenchmarkGroup noGroup = new OutputBenchmarkGroup(outputBenchmarksWithoutGroup
                .toArray(new OutputBenchmark[0]));
        outputGroups.add(noGroup);

        for (BenchmarkGroup group : outputGroupsMap.keySet()) {
            List<OutputBenchmark> outputBenchmarksForGroup = outputGroupsMap.get(group);

            OutputBenchmarkGroup outputGroup = new OutputBenchmarkGroup(
                    outputBenchmarksForGroup.toArray(new OutputBenchmark[0]), group);

            outputGroups.add(outputGroup);
        }

        return outputGroups;
    }

    private OutputBenchmark buildOutputBenchmark(BenchmarkResult benchmarkResult) {
        Iterable<BenchmarkPropertyResult> propertyResults = benchmarkResult.getPropertiesIterable();
        List<OutputPropertyResult> outputProperties = new LinkedList<>();

        for (BenchmarkPropertyResult propertyResult : propertyResults) {
            OutputPropertyResult outputProperty = new OutputPropertyResult(propertyResult);
            outputProperties.add(outputProperty);
        }

        Benchmark benchmark = benchmarkResult.getBenchmark();

        return new OutputBenchmark(outputProperties.toArray(new OutputPropertyResult[0]), benchmark);
    }


}
