package pacr.benchmarker.services;

public class JobResult {

    private long executionTime;
    private String repository;
    private String commitHash;
    private SystemEnvironment systemEnvironment;
    private BenchmarkingResult benchmarkingResult;

    public JobResult() {
    }

    public JobResult(String repository, String commitHash) {
        this.repository = repository;
        this.commitHash = commitHash;
    }

    public SystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
    }

    public void setSystemEnvironment(SystemEnvironment systemEnvironment) {
        this.systemEnvironment = systemEnvironment;
    }

    public BenchmarkingResult getBenchmarkingResult() {
        return benchmarkingResult;
    }

    public void setBenchmarkingResult(BenchmarkingResult benchmarkingResult) {
        this.benchmarkingResult = benchmarkingResult;
    }

    public String getRepository() {
        return repository;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
