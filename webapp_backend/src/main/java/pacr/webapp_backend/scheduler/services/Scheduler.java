package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IJobProvider;
import pacr.webapp_backend.shared.IJobScheduler;
import pacr.webapp_backend.shared.IObserver;

/**
 * Holds a list of jobs and sorts them according to a scheduling policy.
 */
@Component
public class Scheduler implements IJobProvider, IJobScheduler {

    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

    private static final String CRON_DAILY = "0 0 0 * * *";

    private DynamicPriorityQueue<Job> jobs;

    // holds manually prioritized jobs
    private DynamicPriorityQueue<Job> prioritized;

    private Map<String, JobGroup> groups;

    private Collection<IObserver> observers;

    /**
     * Creates a new scheduler and sets the used scheduling policy.
     */
    public Scheduler() {
        this.jobs = new DynamicPriorityQueue<>(new AdvancedSchedulingAlgorithm());
        this.prioritized = new DynamicPriorityQueue<Job>(new FIFOSchedulingAlgorithm());

        this.groups = new HashMap<>();

        this.observers = new ArrayList<>();
    }

    private void addGroup(@NotNull String groupTitle) {
        if (!containsGroup(groupTitle)) {
            groups.put(groupTitle, new JobGroup(groupTitle));
        }
    }

    private boolean containsGroup(@NotNull String groupTitle) {
        return groups.containsKey(groupTitle);
    }

    private JobGroup getGroup(@NotNull String groupTitle) {
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
    public void returnJob(@NotNull IJob job) {
        Objects.requireNonNull(job, "The returned job cannot be null.");

        addJob(job.getJobGroupTitle(), job.getJobID());
    }

    @Override
    public void addToGroupTimeSheet(@NotNull String groupTitle, long time) {
        if (containsGroup(groupTitle)) {
            JobGroup group = getGroup(groupTitle);

            group.addToTimeSheet(time);
        }
    }

    @Override
    public void addJob(@NotNull String groupTitle, @NotNull String jobID) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null.");
        }

        if (!StringUtils.hasText(jobID)) {
            throw new IllegalArgumentException("The jobID cannot be null.");
        }

        addGroup(groupTitle);
        Job job = new Job(jobID, getGroup(groupTitle));

        jobs.add(job);
        LOGGER.info("Added new job to the queue: " + job.toString());

        updateAll();
    }

    /**
     * Moves a job to the manually prioritized jobs queue.
     * @param groupTitle the title of the job.
     * @param jobID the id of the job.
     * @return if the job was successfully prioritized.
     */
    public boolean givePriorityTo(@NotNull String groupTitle, @NotNull String jobID) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null.");
        }

        if (!StringUtils.hasText(jobID)) {
            throw new IllegalArgumentException("The jobID cannot be null.");
        }

        if (!containsGroup(groupTitle)) {
            return false;
        }

        Job job = new Job(jobID, groups.get(groupTitle));

        if (jobs.contains(job)) {
            jobs.remove(job);
            prioritized.add(job);

            return true;
        }

        return false;
    }

    /**
     * Gets a sorted list of all jobs that are not manually prioritized.
     * @return a list of jobs.
     */
    public List<Job> getJobsQueue() {
        return jobs.getSortedList();
    }

    /**
     * Gets a sorted list of all jobs that are manually prioritized.
     * @return a list of jobs.
     */
    public List<Job> getPrioritizedQueue() {
        return prioritized.getSortedList();
    }

    @Override
    public void subscribe(@NotNull IObserver observer) {
        Objects.requireNonNull(observer);

        observers.add(observer);
    }

    @Override
    public void unsubscribe(@NotNull IObserver observer) {
        Objects.requireNonNull(observer);

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
    @Scheduled(cron = CRON_DAILY)
    void resetJobGroupTimeSheets() {
        removeUnusedGroups();

        for (JobGroup group : groups.values()) {
            group.resetTimeSheet();
        }
    }

    private void removeUnusedGroups() {
        Set<String> toRemove = new HashSet<>(groups.keySet());

        Collection<Job> allJobs = new ArrayList<>();
        allJobs.addAll(jobs);
        allJobs.addAll(prioritized);

        for (Job job : allJobs) {
            final String groupTitle = job.getJobGroupTitle();

            toRemove.remove(groupTitle);
        }

        for (String group : toRemove) {
            groups.remove(group);
        }
    }
}
