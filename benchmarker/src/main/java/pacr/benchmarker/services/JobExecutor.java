package pacr.benchmarker.services;

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

    /**
     * Creates an instance of JobExecutor.
     * @param gitHandler is the git handler used for cloning the repository and for checkouts.
     * @param resultSender sends the job result to the Web-App.
     */
    public JobExecutor(GitHandler gitHandler, IJobResultSender resultSender) {
        this.gitHandler = gitHandler;
        this.resultSender = resultSender;
    }

    /**
     * Initializes the JobExecutor.
     * @param repository is the repository pull URL.
     * @param commitHash is the commit hash.
     */
    public void init(String repository, String commitHash) {
        this.repository = repository;
        this.commitHash = commitHash;
        this.jobDispatcher = new JobDispatcher();
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
