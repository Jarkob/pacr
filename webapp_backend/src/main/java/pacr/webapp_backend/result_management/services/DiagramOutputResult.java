package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Objects;

/**
 * An even smaller version of a benchmarking result that is optimized for information needed in a diagram view.
 */
public class DiagramOutputResult {

    private String commitHash;
    private String commitDate;
    private String authorDate;
    private HashMap<String, ResultWithError> result;
    private String[] parents;
    private String globalError;

    /**
     * Creates a DiagramOutputResult from a commit result and its commit. The result and commit must belong to the same
     * hash.
     * @param commitResult the result. Cannot be null.
     * @param commit the commit. Cannot be null.
     */
    DiagramOutputResult(@NotNull CommitResult commitResult, @NotNull ICommit commit) {
        Objects.requireNonNull(commitResult);
        Objects.requireNonNull(commit);

        if (!commitResult.getCommitHash().equals(commit.getCommitHash())) {
            throw new IllegalArgumentException("result and commit must have the same hash");
        }

        this.commitHash = commitResult.getCommitHash();
        this.commitDate = commit.getCommitDate().toString();
        this.authorDate = commit.getAuthorDate().toString();

        this.result = new HashMap<>();
        for (BenchmarkResult benchmarkResult : commitResult.getBenchmarksIterable()) {
            for (BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertiesIterable()) {
                ResultWithError resultAndErrorMessage;
                if (propertyResult.isError()) {
                    resultAndErrorMessage = new ResultWithError(null, propertyResult.getError());
                } else {
                    resultAndErrorMessage = new ResultWithError(propertyResult.getMedian(), null);
                }
                    result.put(propertyResult.getName(), resultAndErrorMessage);
            }
        }

        this.parents = commit.getParentHashes().toArray(new String[0]);
        this.globalError = commitResult.getGlobalError();
    }

    /**
     * @return a hash map that maps property names to the measured median (or error message) for that property.
     */
    HashMap<String, ResultWithError> getResult() {
        return result;
    }
}
