package pacr.webapp_backend.benchmarker_communication.services;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IJobProvider;
import pacr.webapp_backend.shared.IObserver;
import pacr.webapp_backend.shared.IResultSaver;

import javax.annotation.PostConstruct;

@Component
public class JobHandler implements INewRegistrationListener, IObserver, IJobRegistry {

    private static final Logger LOGGER = LogManager.getLogger(JobHandler.class);

    private final IJobSender jobSender;
    private final IBenchmarkerPool benchmarkerPool;
    private final IJobProvider jobProvider;
    private final IResultSaver resultSaver;

    private final Map<String, IJob> currentJobs;

    // If there is a communication error with a benchmarker the attempts are counted.
    private final Map<String, Integer> executionAttempts;

    /**
     * Initiates a new instance of this class.
     * @param jobSender the {@link IJobSender} of the instance.
     * @param benchmarkerPool the {@link BenchmarkerPool} pool, the instance uses.
     * @param jobProvider the {@link IJobProvider} of the instance.
     * @param resultSaver the {@link IResultSaver} of this instance.
     */
    public JobHandler(final IJobSender jobSender, final IBenchmarkerPool benchmarkerPool,
                      final IJobProvider jobProvider, final IResultSaver resultSaver) {
        this.jobSender = jobSender;
        this.benchmarkerPool = benchmarkerPool;
        this.jobProvider = jobProvider;
        this.resultSaver = resultSaver;

        this.currentJobs = new HashMap<>();
        this.executionAttempts = new HashMap<>();
    }

    @PostConstruct
    private void initialize() {
        this.jobProvider.subscribe(this);
        this.benchmarkerPool.addListener(this);
    }

    /**
     * Incorporates the results into the system and marks the benchmarker as free again.
     * If a new job is available it is given to the benchmarker.
     * @param address the address of the benchmarker.
     * @param result the job result.
     */
    public void receiveBenchmarkingResults(final String address, final JobResult result) {
        if (currentJobs.containsKey(address)) {
            final IJob job = currentJobs.remove(address);

            benchmarkerPool.freeBenchmarker(address);

            if (result != null) {
                jobProvider.addToGroupTimeSheet(result.getRepository(), result.getExecutionTime());

                resultSaver.saveBenchmarkingResults(result);

                LOGGER.info("Received job results for '{}' | '{}' from benchmarker '{}'.",
                        job.getJobGroupTitle(), job.getJobID(), address);

                // start a new job for this benchmarker if one is available
                executeJob();
            } else {
                // the job seems to have failed. Try again.
                jobProvider.returnJob(job);
            }
        } else {
            throw new IllegalArgumentException("'" + address + "' was not tasked with a job.");
        }
    }

    /**
     * Sends a job to an available benchmarker.
     * This method does nothing if no job is available or all benchmarkers are occupied.
     */
    public void executeJob() {
        final IJob job = jobProvider.popJob();

        if (job == null) {
            return;
        }

        if (benchmarkerPool.hasFreeBenchmarkers()) {
            final String address = benchmarkerPool.getFreeBenchmarker();

            if (!canExecute(address)) {
                resetAttempts(address);
                return;
            }

            final BenchmarkerJob benchmarkerJob = new BenchmarkerJob(address, job.getJobGroupTitle(), job.getJobID());

            if (jobSender.sendJob(benchmarkerJob)) {
                LOGGER.info("Sent job to benchmarker {}.", address);
                benchmarkerPool.occupyBenchmarker(address);
                currentJobs.put(address, job);

                resetAttempts(address);
            } else {
                LOGGER.warn("Failed to send job to {}.", address);
                addAttempt(address);
                jobProvider.returnJob(job);
            }
        } else {
            jobProvider.returnJob(job);
        }
    }

    private boolean canExecute(final String address) {
        final int maxAttempts = 100;

        if (!executionAttempts.containsKey(address)) {
            resetAttempts(address);
        }

        return executionAttempts.get(address) < maxAttempts;
    }

    private void addAttempt(final String address) {
        if (!executionAttempts.containsKey(address)) {
            resetAttempts(address);
        }

        final int attempts = executionAttempts.get(address);
        executionAttempts.put(address, attempts + 1);
    }

    private void resetAttempts(final String address) {
        executionAttempts.put(address, 0);
    }

    @Override
    public void update() {
        if (benchmarkerPool.hasFreeBenchmarkers()) {
            executeJob();
        }
    }

    @Override
    public void newRegistration() {
        executeJob();
    }

    /**
     * Gets called when the benchmarker is no longer available.
     * If the benchmarker currently had a job the job is returned to the jobProvider.
     * @param address the address of the benchmarker.
     */
    public void connectionLostFor(final String address) {
        if (currentJobs.containsKey(address)) {
            final IJob job = currentJobs.remove(address);

            jobProvider.returnJob(job);
        }
    }

    @Override
    public IJob getCurrentBenchmarkerJob(final String address) {
        if (!StringUtils.hasText(address)) {
            throw new IllegalArgumentException("The address cannot be null or empty.");
        }

        return currentJobs.get(address);
    }
}
