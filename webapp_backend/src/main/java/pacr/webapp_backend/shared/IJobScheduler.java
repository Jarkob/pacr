package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;

/**
 * Allows the scheduling of jobs.
 */
public interface IJobScheduler {

    /**
     * Adds a new job to the job list with a reference to the given group.
     * @param groupTitle the job group of the job.
     * @param jobID the id of the job.
     */
    void addJob(@NotNull String groupTitle, @NotNull String jobID);

}
