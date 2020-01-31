package pacr.benchmarker.services;

/**
 * Represents a job result.
 * Has a benchmarking result, a system environment and an execution time.
 */
public class JobResult {

    private long executionTime;
    private String repository;
    private String commitHash;
    private SystemEnvironment systemEnvironment;
    private BenchmarkingResult benchmarkingResult;

    /**
     * Initializes an instance of JobResult.
     */
    public JobResult() {
        this.executionTime = 0;
        this.repository = "";
        this.commitHash = "";
        this.systemEnvironment = null;
        this.benchmarkingResult = null;
    }

    /**
     * Initializes an instance of JobResult.
     * @param repository is the repository pull URL.
     * @param commitHash is the commit hash.
     */
    public JobResult(String repository, String commitHash) {
        this.repository = repository;
        this.commitHash = commitHash;
        this.systemEnvironment = SystemEnvironment.getInstance();
    }

    /**
     * @return the system environment.
     */
    public SystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
    }

    /**
     * @param systemEnvironment is the new system environment.
     */
    public void setSystemEnvironment(SystemEnvironment systemEnvironment) {
        this.systemEnvironment = systemEnvironment;
    }

    /**
     * @return the benchmarking result.
     */
    public BenchmarkingResult getBenchmarkingResult() {
        return benchmarkingResult;
    }

    /**
     * @param benchmarkingResult is the benchmarking result.
     */
    public void setBenchmarkingResult(BenchmarkingResult benchmarkingResult) {
        this.benchmarkingResult = benchmarkingResult;
    }

    /**
     * @return the pull URL of the repository.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @return the commit hash of the job.
     */
    public String getCommitHash() {
        return commitHash;
    }

    /**
     * @return the execution time of the job.
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * @param executionTime is the execution time of the job.
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
