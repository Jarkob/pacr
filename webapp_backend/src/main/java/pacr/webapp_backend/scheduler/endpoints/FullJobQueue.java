package pacr.webapp_backend.scheduler.endpoints;


import java.util.List;
import pacr.webapp_backend.scheduler.services.Job;

/**
 * Contains two lists of jobs. The first is for normal jobs and the second
 * holds prioritized jobs.
 */
public class FullJobQueue {

    private List<Job> prioritizedJobs;
    private List<Job> jobs;

    /**
     * Creates a new FullJobQueue.
     * @param prioritizedJobs a list of prioritized jobs.
     * @param jobs a list of normal jobs.
     */
    public FullJobQueue(List<Job> prioritizedJobs, List<Job> jobs) {
        this.prioritizedJobs = prioritizedJobs;
        this.jobs = jobs;
    }

    /**
     * Creates an empty FullJobQueue.
     * Necessary for Spring to work.
     */
    public FullJobQueue() {
    }

    /**
     * @return the list of prioritized jobs.
     */
    public List<Job> getPrioritizedJobs() {
        return prioritizedJobs;
    }

    /**
     * @return the list of normal jobs.
     */
    public List<Job> getJobs() {
        return jobs;
    }
}
