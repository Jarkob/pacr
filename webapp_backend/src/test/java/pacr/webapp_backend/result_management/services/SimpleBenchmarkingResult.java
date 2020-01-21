package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.result_management.SystemEnvironment;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple IBenchmarkingResult implementation for testing purposes.
 */
public class SimpleBenchmarkingResult implements IBenchmarkingResult {

    public static final String BENCHMARK_NAME = "benchmark";
    public static final String COMMIT_HASH = "1325";
    public static final String NO_GLOBAL_ERROR = null;

    private String commitHash;
    private ISystemEnvironment systemEnvironment;
    private Map<String, SimpleBenchmark> benchmarks;
    private String globalError;

    public SimpleBenchmarkingResult(String commitHash, ISystemEnvironment systemEnvironment,
                                    Map<String, SimpleBenchmark> benchmarks, String globalError) {
        this.commitHash = commitHash;
        this.systemEnvironment = systemEnvironment;
        this.benchmarks = benchmarks;
        this.globalError = globalError;
    }

    public SimpleBenchmarkingResult() {
        HashMap<String, SimpleBenchmark> benchmarks = new HashMap<>();
        benchmarks.put(BENCHMARK_NAME, new SimpleBenchmark());

        this.commitHash = COMMIT_HASH;
        this.systemEnvironment = new SystemEnvironment();
        this.benchmarks = benchmarks;
        this.globalError = NO_GLOBAL_ERROR;
    }

    @Override
    public String getCommitHash() {
        return commitHash;
    }

    @Override
    public ISystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
    }

    @Override
    public Map<String, ? extends IBenchmark> getBenchmarks() {
        return benchmarks;
    }

    @Override
    public String getGlobalError() {
        return globalError;
    }

    public void addBenchmark(String name, SimpleBenchmark benchmark) {
        benchmarks.put(name, benchmark);
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public SimpleBenchmark getBenchmark(String name) {
        return benchmarks.get(name);
    }
}
