package pacr.benchmarker.services;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a job result.
 * Has a benchmarking result, a system environment and an execution time.
 */
@Getter @Setter
public class JobResult {

    private long executionTime;
    private String repository;
    private String commitHash;
    private SystemEnvironment systemEnvironment;
    private BenchmarkingResult benchmarkingResult;

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

}
