package pacr.benchmarker.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.benchmarker.services.git.GitHandler;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Executes jobs. Sets the repository up and then dispatches the job.
 */
@Component
public class JobExecutor {

    private IJobResultSender resultSender;
    private GitHandler gitHandler;
    private JobDispatcher jobDispatcher;
    private String relativePathToWorkingDir;

    /**
     * Creates an instance of JobExecutor.
     * @param gitHandler is the git handler used for cloning the repository and for checkouts.
     * @param jobDispatcher dispatches the jobs.
     * @param relativePathToWorkingDir is the relative path from the runner script to the repository working dir.
     */
    public JobExecutor(@NotNull GitHandler gitHandler, @NotNull JobDispatcher jobDispatcher,
                       @NotNull @Value("${relPathToWorkingDir}") String relativePathToWorkingDir) {
        Objects.requireNonNull(gitHandler);
        Objects.requireNonNull(jobDispatcher);
        Objects.requireNonNull(relativePathToWorkingDir);
        this.gitHandler = gitHandler;
        this.jobDispatcher = jobDispatcher;
        this.relativePathToWorkingDir = relativePathToWorkingDir;
    }


    /**
     * @param resultSender the result sender being set.
     */
    public void setResultSender(IJobResultSender resultSender) {
        this.resultSender = resultSender;
    }

    /**
     * Executes a job.
     * @param repositoryURL is the repository URL.
     * @param commitHash is the commit hash.
     */
    public void executeJob(String repositoryURL, String commitHash) {
        Instant start = Instant.now();

        String path = gitHandler.setupRepositoryForBenchmark(repositoryURL, commitHash);

        JobResult result = new JobResult(repositoryURL, commitHash);

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
