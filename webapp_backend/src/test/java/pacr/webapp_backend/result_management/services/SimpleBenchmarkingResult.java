package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

import java.util.Map;

public class SimpleBenchmarkingResult implements IBenchmarkingResult {

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
