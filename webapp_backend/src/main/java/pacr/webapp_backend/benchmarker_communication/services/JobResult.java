package pacr.webapp_backend.benchmarker_communication.services;

import java.util.HashMap;
import java.util.Map;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents the result of a BenchmarkerJob.
 */
public class JobResult implements IBenchmarkingResult {

    private long executionTime;
    private String repository;
    private String commitHash;
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

    @Override
    public Map<String, ? extends IBenchmark> getBenchmarks() {
        if (benchmarkingResult == null) {
            return new HashMap<>();
        }

        return benchmarkingResult.getBenchmarks();
    }

    @Override
    public String getGlobalError() {
        if (benchmarkingResult == null) {
            return null;
        }

        return benchmarkingResult.getGlobalError();
    }

    /**
     * @param benchmarkingResult is the benchmarking result.
     */
    public void setBenchmarkingResult(BenchmarkingResult benchmarkingResult) {
        this.benchmarkingResult = benchmarkingResult;
    }

    /**
     * @return the execution time needed to perform the job in seconds.
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * @return is the repository pull URL of the job result.
     */
    public String getRepository() {
        return repository;
    }

}
