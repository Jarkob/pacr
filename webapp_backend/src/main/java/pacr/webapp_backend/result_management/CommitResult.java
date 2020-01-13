package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all measured benchmark data for one commit. This entity is saved in the database.
 */
public class CommitResult implements IBenchmarkingResult {

    private int id;
    private String commitHash;
    private boolean error;
    private String errorMessage;
    private SystemEnvironment systemEnvironment;
    private BenchmarkResult[] benchmarkResults;

    /**
     * Creates a CommitResult from an IBenchmarkingResult and measurements for benchmarks. Copies error message,
     * commitHash, system environment and the repository from the IBenchmarkingResult.
     * @param result the IBenchmarkingResult
     * @param benchmarkResults the measured data for each benchmark.
     */
    CommitResult(IBenchmarkingResult result, BenchmarkResult[] benchmarkResults) {
        if (result.getGlobalError() != null) {
            this.error = true;
            this.errorMessage = result.getGlobalError();
        } else {
            this.error = false;
            this.errorMessage = null;
        }
        this.commitHash = result.getCommitHash();
        this.systemEnvironment = new SystemEnvironment(result.getSystemEnvironment());
        this.benchmarkResults = benchmarkResults;
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
    public Map<String, IBenchmark> getBenchmarks() {
        Map<String, IBenchmark> benchmarks = new HashMap<>();
        for (BenchmarkResult benchmarkResult : benchmarkResults) {
            benchmarks.put(benchmarkResult.getName(), benchmarkResult);
        }
        return benchmarks;
    }

    @Override
    public String getGlobalError() {
        return errorMessage;
    }

    /**
     * Gets an iterable of all the measurements for each benchmark.
     * @return the iterable BenchmarkResults.
     */
    Iterable<BenchmarkResult> getBenchmarksIterable() {
        return Arrays.asList(benchmarkResults);
    }

    /**
     * Indicates whether a global error occurred while benchmarking the commit.
     * @return true if an error occurred, otherwise false.
     */
    boolean isError() {
        return error;
    }
}
