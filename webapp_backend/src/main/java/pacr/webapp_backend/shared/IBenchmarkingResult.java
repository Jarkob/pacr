package pacr.webapp_backend.shared;

import java.util.Map;

/**
 * Represents a collection of benchmarks that were run for a commit.
 */
public interface IBenchmarkingResult {

    /**
     * @return the id of the repository the commit belongs to. -1 if not set.
     */
    int getRepositoryID();

    /**
     * @return the commit hash of the commit that was benchmarked.
     */
    String getCommitHash();

    /**
     * @return the system environment the commit was benchmarked in.
     */
    ISystemEnvironment getSystemEnvironment();

    /**
     * @return a list of benchmarks that were run associated with their name.
     */
    Map<String, ? extends IBenchmark> getBenchmarks();

    /**
     * @return an error message if there was a general error. otherwise returns null.
     */
    String getGlobalError();

}
