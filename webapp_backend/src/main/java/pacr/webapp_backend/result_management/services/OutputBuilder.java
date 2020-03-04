package pacr.webapp_backend.result_management.services;

import org.springframework.stereotype.Component;
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
     * @return an {@link OutputBenchmarkingResult} copied from the commit and result.
     */
    OutputBenchmarkingResult buildDetailOutput(@NotNull final ICommit commit, @NotNull final CommitResult result) {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(result);

        if (!commit.getCommitHash().equals(result.getCommitHash())) {
            throw new IllegalArgumentException("commit and result must have same commit hash");
        }

        final List<OutputBenchmark> outputBenchmarks = buildOutputBenchmarks(result);

        return new OutputBenchmarkingResult(commit, result, outputBenchmarks.toArray(new OutputBenchmark[0]));
    }

    /**
     * Creates OutputBenchmarkingResult based on a commit that has no result.
     * @param commit the commit. Cannot be null.
     * @return an {@link OutputBenchmarkingResult} copied from the commit and with no result data.
     */
    OutputBenchmarkingResult buildDetailOutput(@NotNull final ICommit commit) {
        Objects.requireNonNull(commit);

        return new OutputBenchmarkingResult(commit);
    }

    /**
     * Creates DiagramOutputResult based on a commit and its result. The result must belong to the commit (which means
     * they refer to the same commit hash).
     * @param commit the commit. Cannot be null.
     * @param result the result. Cannot be null.
     * @return a {@link DiagramOutputResult} copied from the commit and result.
     */
    DiagramOutputResult buildDiagramOutput(@NotNull final ICommit commit, @NotNull final CommitResult result, final int benchmarkId) {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(result);

        return new DiagramOutputResult(result, commit, benchmarkId);
    }

    /**
     * Creates DiagramOutputResult based on a commit that has no result.
     * @param commit the commit. Cannot be null.
     * @return a {@link DiagramOutputResult} copied from the commit and with no result data.
     */
    DiagramOutputResult buildDiagramOutput(@NotNull final ICommit commit) {
        Objects.requireNonNull(commit);

        return new DiagramOutputResult(commit);
    }

    private List<OutputBenchmark> buildOutputBenchmarks(final CommitResult result) {
        final Iterable<BenchmarkResult> benchmarkResults = result.getBenchmarkResults();

        final List<OutputBenchmark> outputBenchmarks = new LinkedList<>();

        for (final BenchmarkResult benchmarkResult : benchmarkResults) {
            final OutputBenchmark outputBenchmark = buildOutputBenchmark(benchmarkResult);

            outputBenchmarks.add(outputBenchmark);
        }

        return outputBenchmarks;
    }

    private OutputBenchmark buildOutputBenchmark(final BenchmarkResult benchmarkResult) {
        final Iterable<BenchmarkPropertyResult> propertyResults = benchmarkResult.getPropertyResults();
        final List<OutputPropertyResult> outputProperties = new LinkedList<>();

        for (final BenchmarkPropertyResult propertyResult : propertyResults) {
            final OutputPropertyResult outputProperty = new OutputPropertyResult(propertyResult);
            outputProperties.add(outputProperty);
        }

        final Benchmark benchmark = benchmarkResult.getBenchmark();

        return new OutputBenchmark(outputProperties.toArray(new OutputPropertyResult[0]), benchmark);
    }


}
