package pacr.benchmarker.services;

import org.springframework.stereotype.Component;
import pacr.benchmarker.services.git.GitHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Component
public class JobExecutor implements Runnable {

    private IJobResultSender resultSender;

    private GitHandler gitHandler;

    private JobDispatcher jobDispatcher;
    private String repository;
    private String commitHash;

    public JobExecutor(GitHandler gitHandler) {
        this.gitHandler = gitHandler;
    }

    public void init(String repository, String commitHash, IJobResultSender resultSender) {
        this.repository = repository;
        this.commitHash = commitHash;
        this.resultSender = resultSender;
        this.jobDispatcher = new JobDispatcher();
    }

    @Override
    public void run() {
        Instant start = Instant.now();

        String path = gitHandler.setupRepositoryForBenchmark(repository, commitHash);

        JobResult result = new JobResult(repository, commitHash);

        SystemEnvironment environment = SystemEnvironment.getInstance();
        result.setSystemEnvironment(environment);

        try {
            BenchmarkingResult benchmarkingResult;

            benchmarkingResult = jobDispatcher.dispatchJob(path);

            result.setBenchmarkingResult(benchmarkingResult);

            Instant end = Instant.now();

            long executionTime = Duration.between(start, end).getSeconds();
            result.setExecutionTime(executionTime);

            resultSender.sendJobResults(result);
        } catch (IOException | InterruptedException e) {
            // todo: handle error
            System.out.println(e.getMessage());
        }
    }
}
