package pacr.webapp_backend.benchmarker_communication.services;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IJobProvider;
import pacr.webapp_backend.shared.IObserver;
import pacr.webapp_backend.shared.IResultSaver;

@Component
public class JobHandler implements INewRegistrationListener, IObserver {

    private IJobSender jobSender;
    private IBenchmarkerPool benchmarkerPool;
    private IJobProvider jobProvider;
    private IResultSaver resultSaver;

    private Map<String, IJob> currentJobs;

    // If there is a communication error with a benchmarker the attempts are counted.
    private Map<String, Integer> executionAttempts;

    public JobHandler(IJobSender jobSender, IBenchmarkerPool benchmarkerPool,
                        IJobProvider jobProvider, IResultSaver resultSaver) {
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
    public void receiveBenchmarkingResults(String address, JobResult result) {
        if (currentJobs.containsKey(address)) {
            IJob job = currentJobs.remove(address);

            benchmarkerPool.freeBenchmarker(address);

            if (result != null) {
                resultSaver.saveBenchmarkingResults(result);

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
        IJob job = jobProvider.popJob();

        if (job == null) {
            return;
        }

        if (benchmarkerPool.hasFreeBenchmarkers()) {
            String address = benchmarkerPool.getFreeBenchmarker();

            if (!canExecute(address)) {
                resetAttempts(address);
                return;
            }

            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(address, job.getJobGroupTitle(), job.getJobID());

            if (jobSender.sendJob(benchmarkerJob)) {
                benchmarkerPool.occupyBenchmarker(address);
                currentJobs.put(address, job);

                resetAttempts(address);
            } else {
                addAttempt(address);
                jobProvider.returnJob(job);
            }
        } else {
            jobProvider.returnJob(job);
        }
    }

    private boolean canExecute(String address) {
        assert (address != null && !address.isEmpty() && !address.isBlank());

        final int maxAttempts = 100;

        if (!executionAttempts.containsKey(address)) {
            resetAttempts(address);
        }

        return executionAttempts.get(address) < maxAttempts;
    }

    private void addAttempt(String address) {
        assert (address != null && !address.isEmpty() && !address.isBlank());

        if (!executionAttempts.containsKey(address)) {
            resetAttempts(address);
        }

        int attempts = executionAttempts.get(address);
        executionAttempts.put(address, attempts + 1);
    }

    private void resetAttempts(String address) {
        assert (address != null && !address.isEmpty() && !address.isBlank());

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
    public void connectionLostFor(String address) {
        if (currentJobs.containsKey(address)) {
            IJob job = currentJobs.remove(address);

            jobProvider.returnJob(job);
        }
    }

}
