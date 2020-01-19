package pacr.webapp_backend.import_export.servies;

import java.util.Map;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents a collection of benchmarks that were run for a commit.
 */
public class BenchmarkingResult implements IBenchmarkingResult {

    private String commitHash;
    private Map<String, Benchmark> benchmarks;
    private String globalError;
    private SystemEnvironment systemEnvironment;

    /**
     * Creates an empty BenchmarkingResult.
     *
     * Needed for Spring to work.
     */
    public BenchmarkingResult() {
    }

    @Override
    public int getRepositoryID() {
        return -1;
    }

    @Override
    public String getCommitHash() {
        return commitHash;
    }

    @Override
    public ISystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
    }

    /**
     * @return a list of benchmarks that were run associated with their name.
     */
    public Map<String, ? extends IBenchmark> getBenchmarks() {
        return benchmarks;
    }

    /**
     * @return an error message if there was a general error. Null is returned if there was no error.
     */
    public String getGlobalError() {
        if (globalError.isEmpty() || globalError.isBlank()) {
            return null;
        }

        return globalError;
    }
}
