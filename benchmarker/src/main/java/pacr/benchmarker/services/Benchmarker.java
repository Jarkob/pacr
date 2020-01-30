package pacr.benchmarker.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Represents a benchmarker that executes benchmarking jobs.
 */
@Component
public class Benchmarker {

    private static final Logger LOGGER = LogManager.getLogger(Benchmarker.class);

    private JobExecutor jobExecutor;

    /**
     * Initializes an instance of Benchmarker.
     * @param jobExecutor is the job executor for executing the jobs.
     */
    public Benchmarker(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    /**
     * Executes a job.
     * @param repository is the pull URL of the repository.
     * @param commitHash is the commit hash.
     */
    public void executeJob(String repository, String commitHash) {

        LOGGER.info("Executing job at repository {} and commit {}.", repository, commitHash);

        jobExecutor.init(repository, commitHash);

        Thread executionThread = new Thread(jobExecutor);

        executionThread.start();
    }

    /**
     * @param resultSender is the result sender being set.
     */
    public void setResultSender(IJobResultSender resultSender) {
        this.jobExecutor.setResultSender(resultSender);
    }

}
