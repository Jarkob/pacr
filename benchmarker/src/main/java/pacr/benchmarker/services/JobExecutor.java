package pacr.benchmarker.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.benchmarker.services.git.GitHandler;

import java.time.Duration;
import java.time.Instant;

/**
 * Executes jobs.
 */
@Component
public class JobExecutor implements Runnable {

    private IJobResultSender resultSender;
    private GitHandler gitHandler;
    private JobDispatcher jobDispatcher;
    private String repository;
    private String commitHash;
    private String relativePathToWorkingDir;

    /**
     * Creates an instance of JobExecutor.
     * @param gitHandler is the git handler used for cloning the repository and for checkouts.
     * @param jobDispatcher dispatches the jobs.
     * @param relativePathToWorkingDir is the relative path from the runner script to the repository working dir.
     */
    public JobExecutor(GitHandler gitHandler, JobDispatcher jobDispatcher,
                       @Value("${relPathToWorkingDir}") String relativePathToWorkingDir) {
        this.gitHandler = gitHandler;
        this.jobDispatcher = jobDispatcher;
        this.relativePathToWorkingDir = relativePathToWorkingDir;
    }

    /**
     * Initializes the JobExecutor.
     * @param repository is the repository pull URL.
     * @param commitHash is the commit hash.
     */
    public void init(String repository, String commitHash) {
        this.repository = repository;
        this.commitHash = commitHash;
    }

    /**
     * @param resultSender the result sender being set.
     */
    public void setResultSender(IJobResultSender resultSender) {
        this.resultSender = resultSender;
    }

    @Override
    public void run() {
        Instant start = Instant.now();

        String path = gitHandler.setupRepositoryForBenchmark(repository, commitHash);

        JobResult result = new JobResult(repository, commitHash);

        BenchmarkingResult benchmarkingResult;
        if (path == null) { // cloning didn't work
            benchmarkingResult = new BenchmarkingResult();
            benchmarkingResult.setGlobalError("Could not set up repository for benchmarking.");
        } else {
            path = relativePathToWorkingDir + path;

            // fetch benchmarking result
            benchmarkingResult = jobDispatcher.dispatchJob(path);
            result.setBenchmarkingResult(benchmarkingResult);
        }

        Instant end = Instant.now();

        // set execution time
        long executionTime = Duration.between(start, end).getSeconds();
        result.setExecutionTime(executionTime);

        resultSender.sendJobResults(result);
    }
}
