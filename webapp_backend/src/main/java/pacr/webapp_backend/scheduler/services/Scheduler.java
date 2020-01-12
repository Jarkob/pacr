package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IJobProvider;
import pacr.webapp_backend.shared.IJobScheduler;
import pacr.webapp_backend.shared.IObserver;

/**
 * Holds a list of jobs and sorts them according to a scheduling policy.
 */
@Component
public class Scheduler implements IJobProvider, IJobScheduler {

    private PriorityQueue<Job> jobs;

    // holds manually prioritized jobs
    private PriorityQueue<Job> prioritized;

    private Map<String, JobGroup> groups;

    private Collection<IObserver> observers;

    /**
     * Creates a new scheduler and sets the used scheduling policy.
     */
    Scheduler() {
        this.jobs = new PriorityQueue<Job>(new AdvancedSchedulingAlgorithm());
        this.prioritized = new PriorityQueue<Job>(new FIFOSchedulingAlgorithm());

        this.groups = new HashMap<>();

        this.observers = new ArrayList<>();
    }

    private void addGroup(String groupTitle) {
        if (!containsGroup(groupTitle)) {
            groups.put(groupTitle, new JobGroup(groupTitle));
        }
    }
    private boolean containsGroup(String groupTitle) {
        return groups.containsKey(groupTitle);
    }

    private JobGroup getGroup(String groupTitle) {
        return groups.get(groupTitle);
    }

    @Override
    public IJob popJob() {
        if (!prioritized.isEmpty()) {
            return prioritized.poll();
        }

        return jobs.poll();
    }

    @Override
    public void returnJob(IJob job) {
        addJob(job.getJobID(), job.getJobGroupTitle());
    }

    @Override
    public void addToGroupTimeSheet(String groupTitle, long time) {
        if (containsGroup(groupTitle)) {
            JobGroup group = getGroup(groupTitle);

            group.updateTimeSheet(time);
        }
    }

    @Override
    public void addJob(String groupTitle, String jobID) {
        assert (groupTitle != null && !groupTitle.isEmpty());
        assert (jobID != null && !jobID.isEmpty());

        addGroup(groupTitle);
        Job job = new Job(jobID, getGroup(groupTitle));

        jobs.add(job);

        updateAll();
    }

    /**
     * Moves a job to the manually prioritized jobs queue.
     * @param groupTitle the title of the job.
     * @param jobID the id of the job.
     */
    public void givePriorityTo(String groupTitle, String jobID) {
        for (Job job : jobs) {
            if (job.getJobGroupTitle().equals(groupTitle) && job.getJobID().equals(jobID)) {
                jobs.remove(job);
                prioritized.add(job);
            }
        }
    }

    /**
     * Gets a sorted list of all jobs that are not manually prioritized.
     * @return a list of jobs.
     */
    public List<Job> getJobsQueue() {
        return getSortedListFrom(jobs);
    }

    /**
     * Gets a sorted list of all jobs that are manually prioritized.
     * @return a list of jobs.
     */
    public List<Job> getPrioritizedQueue() {
        return getSortedListFrom(prioritized);
    }

    private List<Job> getSortedListFrom(PriorityQueue<Job> priorityQueue) {
        PriorityQueue<Job> queue = new PriorityQueue<>(priorityQueue.comparator());
        queue.addAll(priorityQueue);

        List<Job> sortedList = new ArrayList<>();

        // PriorityQueue is not necessarily sorted
        while (!queue.isEmpty()) {
            Job job = queue.poll();

            sortedList.add(job);
        }

        return sortedList;
    }

    @Override
    public void subscribe(IObserver observer) {
        assert (observer != null);

        observers.add(observer);
    }

    @Override
    public void unsubscribe(IObserver observer) {
        assert (observer != null);

        observers.remove(observer);
    }

    @Override
    public void updateAll() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }

    /**
     * Resets the groups field daily.
     */
    @Scheduled(cron = "@daily")
    private void resetJobGroupTimeSheets() {
        groups.clear();
    }
}
