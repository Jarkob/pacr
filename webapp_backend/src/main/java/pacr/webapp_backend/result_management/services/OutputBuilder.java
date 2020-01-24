package pacr.webapp_backend.result_management.services;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.OutputBenchmark;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
import pacr.webapp_backend.result_management.OutputPropertyResult;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
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
    OutputBenchmarkingResult buildDetailOutput(@NotNull ICommit commit, @NotNull CommitResult result) {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(result);

        if (!commit.getCommitHash().equals(result.getCommitHash())) {
            throw new IllegalArgumentException("commit and result must have same commit hash");
        }

        List<OutputBenchmark> outputBenchmarks = buildOutputBenchmarks(result);

        return new OutputBenchmarkingResult(commit, result, outputBenchmarks.toArray(new OutputBenchmark[0]));
    }

    /**
     * Creates DiagramOutputResult based on a commit and its result. The result must belong to the commit (which means
     * they refer to the same commit hash).
     * @param commit the commit. Cannot be null.
     * @param result the result. Cannot be null.
     * @return a DiagramOutputResult copied from the commit and result.
     */
    DiagramOutputResult buildDiagramOutput(@NotNull ICommit commit, @NotNull CommitResult result) {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(result);

        return new DiagramOutputResult(result, commit);
    }

    private List<OutputBenchmark> buildOutputBenchmarks(CommitResult result) {
        Iterable<BenchmarkResult> benchmarkResults = result.getBenchmarksIterable();

        List<OutputBenchmark> outputBenchmarks = new LinkedList<>();

        for (BenchmarkResult benchmarkResult : benchmarkResults) {
            OutputBenchmark outputBenchmark = buildOutputBenchmark(benchmarkResult);

            outputBenchmarks.add(outputBenchmark);
        }

        return outputBenchmarks;
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
