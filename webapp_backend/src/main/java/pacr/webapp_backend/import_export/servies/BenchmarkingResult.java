package pacr.webapp_backend.import_export.servies;

import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;
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

    /**
     * Creates a BenchmarkingResult from an IBenchmarkingResult interface.
     *
     * @param result the IBenchmarkingResult which is used to create the BenchmarkingResult.
     */
    public BenchmarkingResult(IBenchmarkingResult result) {
        this.commitHash = result.getCommitHash();
        this.globalError = result.getGlobalError();
        this.systemEnvironment = new SystemEnvironment(result.getSystemEnvironment());

        this.benchmarks = new HashMap<>();
        Map<String, ? extends IBenchmark> resultBenchmarks = result.getBenchmarks();
        for (String benchmarkName : resultBenchmarks.keySet()) {
            this.benchmarks.put(benchmarkName, new Benchmark(resultBenchmarks.get(benchmarkName)));
        }
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
        if (!StringUtils.hasText(globalError)) {
            return null;
        }

        return globalError;
    }
}
