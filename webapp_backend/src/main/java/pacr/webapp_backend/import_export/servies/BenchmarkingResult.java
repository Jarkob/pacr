package pacr.webapp_backend.import_export.servies;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents a collection of benchmarks that were run for a commit.
 */
@NoArgsConstructor
public class BenchmarkingResult implements IBenchmarkingResult {

    @Getter
    private String commitHash;

    private SystemEnvironment systemEnvironment;
    private Map<String, Benchmark> benchmarks;
    private String globalError;

    /**
     * Creates a BenchmarkingResult from an IBenchmarkingResult interface.
     *
     * @param result the IBenchmarkingResult which is used to create the BenchmarkingResult.
     */
    public BenchmarkingResult(final IBenchmarkingResult result) {
        this.commitHash = result.getCommitHash();
        this.globalError = result.getGlobalError();
        this.systemEnvironment = new SystemEnvironment(result.getSystemEnvironment());

        this.benchmarks = new HashMap<>();
        final Map<String, ? extends IBenchmark> resultBenchmarks = result.getBenchmarks();
        for (final String benchmarkName : resultBenchmarks.keySet()) {
            this.benchmarks.put(benchmarkName, new Benchmark(resultBenchmarks.get(benchmarkName)));
        }
    }

    @Override
    public int getRepositoryID() {
        return -1;
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
     * @return an error message if there was a general error. {@code null} is returned if there was no error.
     */
    public String getGlobalError() {
        if (!StringUtils.hasText(globalError)) {
            return null;
        }

        return globalError;
    }
}
