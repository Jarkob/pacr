package pacr.webapp_backend.scheduler.services;

import java.util.Collection;

/**
 * Saves jobs persistently and gives access to them.
 */
public interface IJobAccess {

    /**
     * Finds all jobs which have the prioritized value set.
     *
     * @param prioritized all found jobs have this value as prioritized.
     *
     * @return a list of jobs.
     */
    Collection<Job> findAllByPrioritized(boolean prioritized);

    /**
     * Saves a job.
     *
     * @param job the job which is saved.
     */
    void saveJob(Job job);

    /**
     * Deletes a job from the storage.
     *
     * @param job the job which is deleted.
     */
    void deleteJob(Job job);

}
