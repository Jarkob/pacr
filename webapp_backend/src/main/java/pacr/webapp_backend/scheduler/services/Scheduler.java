package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
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

    private IJobAccess jobAccess;
    private IJobGroupAccess jobGroupAccess;

    /**
     * Creates a new scheduler and sets the used scheduling policy.
     *
     * @param jobAccess the access interface to store and retrieve jobs.
     * @param jobGroupAccess the access interface to retrieve job groups.
     */
    public Scheduler(IJobAccess jobAccess, IJobGroupAccess jobGroupAccess) {
        this.jobAccess = jobAccess;
        this.jobGroupAccess = jobGroupAccess;

        this.jobs = new DynamicPriorityQueue<>(new AdvancedSchedulingAlgorithm());
        this.prioritized = new DynamicPriorityQueue<Job>(new FIFOSchedulingAlgorithm());

        this.groups = new HashMap<>();

        this.observers = new ArrayList<>();
    }

    @PostConstruct
    private void loadJobsFromStorage() {
        prioritized.addAll(jobAccess.findAllByPrioritized(true));
        jobs.addAll(jobAccess.findAllByPrioritized(false));

        for (JobGroup group : jobGroupAccess.findAllJobGroups()) {
            groups.put(group.getTitle(), group);
        }
    }

    private JobGroup addGroup(@NotNull String groupTitle) {
        JobGroup group;
        if (!containsGroup(groupTitle)) {
            group = new JobGroup(groupTitle);
            jobGroupAccess.saveJobGroup(group);
            groups.put(groupTitle, group);
        } else {
            group = groups.get(groupTitle);
        }

        return group;
    }

    private boolean containsGroup(@NotNull String groupTitle) {
        return groups.containsKey(groupTitle);
    }

    private JobGroup getGroup(@NotNull String groupTitle) {
        return groups.get(groupTitle);
    }

    @Override
    public IJob popJob() {
        Job job;
        if (!prioritized.isEmpty()) {
            job = prioritized.poll();
        } else {
            job = jobs.poll();
        }

        if (job != null) {
            jobAccess.deleteJob(job);
        }

        return job;
    }

    @Override
    public void returnJob(@NotNull IJob job) {
        Objects.requireNonNull(job, "The returned job cannot be null.");

        LOGGER.info("Job {} | {} was returned to the queue.", job.getJobGroupTitle(), job.getJobID());

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

        JobGroup group = addGroup(groupTitle);
        Job job = new Job(jobID, group);

        jobAccess.saveJob(job);
        jobs.add(job);

        updateAll();
    }

    @Override
    public void addJobs(@NotNull String groupTitle, @NotNull Collection<String> jobIDs) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null or empty.");
        }
        Objects.requireNonNull(jobIDs, "The jobIds cannot be null.");

        Collection<Job> jobsToAdd = new ArrayList<>();
        JobGroup group = addGroup(groupTitle);

        for (String jobID : jobIDs) {
            jobsToAdd.add(new Job(jobID, group));
        }

        jobs.addAll(jobsToAdd);
        jobAccess.saveJobs(jobsToAdd);

        LOGGER.info("Added {} jobs to the queue.", jobsToAdd.size());

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

        for (Job job : jobs) {
            if (job.getJobGroupTitle().equals(groupTitle) && job.getJobID().equals(jobID)) {
                jobs.remove(job);
                job.setPrioritized(true);
                prioritized.add(job);
                jobAccess.saveJob(job);

                LOGGER.info(job.getJobGroupTitle() + " | " + job.getJobID() + " was prioritized.");

                return true;
            }
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

        LOGGER.info("Daily job group reset finished.");
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
