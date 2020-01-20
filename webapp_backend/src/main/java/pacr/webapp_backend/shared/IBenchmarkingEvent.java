package pacr.webapp_backend.shared;

/**
 * Holds metadata for a benchmarking event.
 */
public interface IBenchmarkingEvent {
    /**
     * @return the hash of the commit that was benchmarked. Is not null, empty or blank.
     */
    String getCommitHash();

    /**
     * @return The name of the repository of the commit. Is not null, empty or blank.
     */
    String getRepositoryName();

    /**
     * @return the hash of the commit that was used for comparison. May be null. In this case
     * no comparison data was found.
     */
    String getComparisonCommitHash();

    /**
     * @return the average improvement of the measurements of the commit compared to the comparison commit.
     */
    int getAverageImprovement();

    /**
     * @return An error message in case there was a global error while benchmarking the commit, otherwise null.
     */
    String getGlobalError();
}
