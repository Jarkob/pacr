package pacr.webapp_backend.shared;

/**
 * An interface for a job.
 */
public interface IJob {

    /**
     * @return the id of the job.
     */
    String getJobID();

    /**
     * @return the title of the job's group.
     */
    String getJobGroupTitle();

}
