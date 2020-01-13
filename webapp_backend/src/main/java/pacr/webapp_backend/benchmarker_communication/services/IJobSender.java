package pacr.webapp_backend.benchmarker_communication.services;

/**
 * Sends jobs to a selected PACR-Benchmarker.
 */
public interface IJobSender {

    /**
     * Sends a job to the selected benchmarker.
     * @param benchmarkerJob the job for a selected benchmarker.
     * @return if the job was sent successfully.
     */
    boolean sendJob(BenchmarkerJob benchmarkerJob);

}
