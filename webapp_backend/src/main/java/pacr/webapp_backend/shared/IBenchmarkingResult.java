package pacr.webapp_backend.shared;

import java.util.Map;

/**
 * Represents a collection of benchmarks that were run for a commit.
 */
public interface IBenchmarkingResult {

    /**
     * @return the pull-url of the repository the commit belongs to.
     */
    String getRepository();

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
    Map<String, IBenchmark> getBenchmarks();

    /**
     * @return an error message if there was a general error.
     */
    String getGlobalError();

}
