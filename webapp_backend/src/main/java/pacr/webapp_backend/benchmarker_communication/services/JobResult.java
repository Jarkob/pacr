package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Map;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents the result of a BenchmarkerJob.
 */
public class JobResult implements IBenchmarkingResult {

    private String repository;
    private String commitHash;
    private long executionTime;
    private SystemEnvironment systemEnvironment;
    private BenchmarkingResult benchmarkingResult;

    /**
     * Creates an empty JobResult.
     *
     * Needed for Spring to work.
     */
    public JobResult() {
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
        return benchmarkingResult.getBenchmarks();
    }

    @Override
    public String getGlobalError() {
        return benchmarkingResult.getGlobalError();
    }

    /**
     * @return the execution time needed to perform the job in seconds.
     */
    public long getExecutionTime() {
        return executionTime;
    }
}
