package pacr.webapp_backend.scheduler.services;

import java.util.Collection;
import java.util.List;

/**
 * Saves jobs persistently and gives access to them.
 */
public interface IJobAccess {

    List<Job> findJobs();

    List<Job> findPrioritized();

    Collection<Job> findJobs(String groupTitle);

    /**
     * Saves a job.
     *
     * @param job the job which is saved.
     */
    void saveJob(Job job);

    /**
     * Saves all given jobs.
     *
     * @param jobs the jobs which are saved.
     */
    void saveJobs(Collection<Job> jobs);

    /**
     * Deletes a job from the storage.
     *
     * @param job the job which is deleted.
     */
    void deleteJob(Job job);

    /**
     * Deletes a list of jobs from the storage.
     *
     * @param jobs the list of jobs to be removed.
     */
    void deleteJobs(Collection<Job> jobs);

}
