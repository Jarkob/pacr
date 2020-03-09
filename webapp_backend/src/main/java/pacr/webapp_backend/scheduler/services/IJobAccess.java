package pacr.webapp_backend.scheduler.services;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Saves jobs persistently and gives access to them.
 */
public interface IJobAccess {

    /**
     * Gets all jobs which aren't prioritized.
     * @param pageable contains paging information.
     * @return a page of jobs.
     */
    Page<Job> findJobs(Pageable pageable);

    /**
     * @return all jobs which aren't prioritized.
     */
    List<Job> findJobs();

    /**
     * Gets all prioritized jobs.
     * @param pageable contains paging information.
     * @return a page of jobs.
     */
    Page<Job> findPrioritized(Pageable pageable);

    /**
     * @return all prioritized jobs.
     */
    List<Job> findPrioritized();

    /**
     * Gets all jobs belonging to the given group.
     * @param groupTitle the title of the group.
     * @return a list of jobs belonging to the group.
     */
    Collection<Job> findAllJobs(String groupTitle);

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
    void deleteJobs(Collection<? extends Job> jobs);

}
