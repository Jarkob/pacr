package pacr.webapp_backend.result_management.services;

import lombok.Getter;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
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
    private String[] parents;
    private String[] labels;
    private String globalError;

    /**
     * Creates a DiagramOutputResult from a specific benchmark of a commit result and its commit. The result and commit
     * must belong to the same hash.
     * @param commitResult the result. Cannot be null.
     * @param commit the commit. Cannot be null.
     * @param benchmarkId the id of the benchmark that is supposed to be included in this DiagramOutputResult.
     */
    DiagramOutputResult(@NotNull CommitResult commitResult, @NotNull ICommit commit, int benchmarkId) {
        Objects.requireNonNull(commitResult);
        Objects.requireNonNull(commit);

        if (!commitResult.getCommitHash().equals(commit.getCommitHash())) {
            throw new IllegalArgumentException("result and commit must have the same hash");
        }

        this.commitHash = commitResult.getCommitHash();
        this.commitDate = commit.getCommitDate().toString();
        this.authorDate = commit.getAuthorDate().toString();

        this.result = new HashMap<>();
        for (BenchmarkResult benchmarkResult : commitResult.getBenchmarkResults()) {
            if (benchmarkResult.getBenchmark().getId() == benchmarkId) {
                for (BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                    ResultWithError resultAndErrorMessage;
                    if (propertyResult.isError()) {
                        resultAndErrorMessage = new ResultWithError(null, propertyResult.getError());
                    } else {
                        resultAndErrorMessage = new ResultWithError(propertyResult.getMedian(), null);
                    }
                    result.put(propertyResult.getName(), resultAndErrorMessage);
                }
                break;
            }
        }

        this.labels = commit.getLabels().toArray(new String[0]);
        this.parents = commit.getParentHashes().toArray(new String[0]);
        this.globalError = commitResult.getGlobalError();
    }

    /**
     * Creates a DiagramOutputResult for a commit that has no results. Adds a global error due to this.
     * @param commit the commit. Cannot be null.
     */
    DiagramOutputResult(@NotNull ICommit commit) {
        Objects.requireNonNull(commit);

        this.commitHash = commit.getCommitHash();
        this.commitDate = commit.getCommitDate().toString();
        this.authorDate = commit.getAuthorDate().toString();

        this.result = null;

        this.labels = commit.getLabels().toArray(new String[0]);
        this.parents = commit.getParentHashes().toArray(new String[0]);
        this.globalError = null;
    }
}
