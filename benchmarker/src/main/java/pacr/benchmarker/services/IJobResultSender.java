package pacr.benchmarker.services;

/**
 * Sends the job results to the Web-App.
 */
public interface IJobResultSender {

    /**
     * @param result is the job result being sent.
     */
    void sendJobResults(JobResult result);

}
