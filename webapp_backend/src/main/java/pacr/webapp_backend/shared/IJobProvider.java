package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;

/**
 * Provides methods to get and return jobs. It also allows to update
 * the time sheet of a job group.
 *
 * This interface uses the Observer Pattern to notify observers about new jobs.
 */
public interface IJobProvider extends ISubject {

    /**
     * Returns a new job and removes it from the job list.     *
     * @return a job or null if the job list is empty.
     */
    IJob popJob();

    /**
     * Returns the given job and adds it to the job list again.
     * @param job the job to be returned.
     */
    void returnJob(@NotNull IJob job);

    /**
     * Adds the given time to the time sheet of the given group.
     * @param groupTitle the groupTitle of the group.
     * @param time the time that is added in seconds.
     */
    void addToGroupTimeSheet(@NotNull String groupTitle, long time);

}
