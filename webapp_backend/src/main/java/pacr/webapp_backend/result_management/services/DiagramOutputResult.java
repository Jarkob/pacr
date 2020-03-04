package pacr.webapp_backend.result_management.services;

import lombok.Getter;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * An even smaller version of a benchmarking result that is optimized for information needed in a diagram view.
 * '@Getter' provides this class with all getters so the json can be properly created.
 */
@Getter
public class DiagramOutputResult {

    private String commitHash;
    private String commitDate;
    private String authorDate;
    private HashMap<String, ResultWithError> result;
    private Collection<String> parents;
    private Collection<String> labels;
    private String globalError;

    /**
     * Creates a DiagramOutputResult from a specific benchmark of a commit result and its commit. The result and commit
     * must belong to the same commit hash.
     * @param commitResult the result. Cannot be null.
     * @param commit the commit. Cannot be null.
     * @param benchmarkId the id of the benchmark that is supposed to be included in this DiagramOutputResult.
     */
    DiagramOutputResult(@NotNull final CommitResult commitResult, @NotNull final ICommit commit, final int benchmarkId) {
        Objects.requireNonNull(commitResult);
        Objects.requireNonNull(commit);

        if (!commitResult.getCommitHash().equals(commit.getCommitHash())) {
            throw new IllegalArgumentException("result and commit must have the same hash");
        }

        this.commitHash = commitResult.getCommitHash();
        this.commitDate = commit.getCommitDate().toString();
        this.authorDate = commit.getAuthorDate().toString();

        this.result = new HashMap<>();
        for (final BenchmarkResult benchmarkResult : commitResult.getBenchmarkResults()) {
            if (benchmarkResult.getBenchmark().getId() == benchmarkId) {
                for (final BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                    result.put(propertyResult.getName(), new ResultWithError(propertyResult));
                }
                break;
            }
        }

        this.labels = commit.getLabels();
        this.parents = commit.getParentHashes();
        this.globalError = commitResult.getGlobalError();
    }

    /**
     * Creates a DiagramOutputResult for a commit that has no results. Adds a global error due to this.
     * @param commit the commit. Cannot be null.
     */
    DiagramOutputResult(@NotNull final ICommit commit) {
        Objects.requireNonNull(commit);

        this.commitHash = commit.getCommitHash();
        this.commitDate = commit.getCommitDate().toString();
        this.authorDate = commit.getAuthorDate().toString();

        this.result = null;

        this.labels = commit.getLabels();
        this.parents = commit.getParentHashes();
        this.globalError = null;
    }
}
