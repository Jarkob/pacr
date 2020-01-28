package pacr.benchmarker.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.benchmarker.endpoints.BenchmarkerSessionHandler;
import pacr.benchmarker.services.git.GitHandler;

@Component
public class Benchmarker {

    private static final Logger LOGGER = LogManager.getLogger(Benchmarker.class);

    private JobExecutor jobExecutor;

    public Benchmarker(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    public void executeJob(String repository, String commitHash, IJobResultSender resultSender) {

        LOGGER.info("Executing job at repository {} and commit {}.", repository, commitHash);

        jobExecutor.init(repository, commitHash, resultSender);

        Thread executionThread = new Thread(jobExecutor);

        executionThread.start();
    }

}
