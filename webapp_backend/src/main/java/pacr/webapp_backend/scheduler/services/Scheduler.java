package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
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

    private DynamicPriorityQueue<JobGroup> groupQueue;

    private PriorityQueue<Job> prioritizedQueue;

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

        this.groupQueue = new DynamicPriorityQueue<>(new GroupSchedulingAlgorithm());
        this.prioritizedQueue = new DynamicPriorityQueue<>(new FIFOSchedulingAlgorithm());

        this.groups = new HashMap<>();

        this.observers = new ArrayList<>();
    }

    @PostConstruct
    void loadJobsFromStorage() {
        prioritizedQueue.addAll(jobAccess.findPrioritized());

        for (JobGroup group : jobGroupAccess.findAllJobGroups()) {
            groupQueue.add(group);
            groups.put(group.getTitle(), group);
        }
    }

    private JobGroup addGroup(@NotNull String groupTitle) {
        JobGroup group;
        if (!containsGroup(groupTitle)) {
            group = new JobGroup(groupTitle);
            jobGroupAccess.saveJobGroup(group);
            groups.put(groupTitle, group);
            groupQueue.add(group);
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
        Job job = null;

        if (!prioritizedQueue.isEmpty()) {
            job = prioritizedQueue.poll();
        } else {
            while (job == null && groupQueue.size() > 0) {
                JobGroup group = groupQueue.peek();

                List<Job> jobs = new ArrayList<>(jobAccess.findJobs(group.getTitle()));

                if (jobs.size() == 0) {
                    removeJobGroup(group.getTitle());
                } else {
                    jobs.sort(new AdvancedSchedulingAlgorithm());

                    job = jobs.stream().findFirst().orElse(null);
                }
            }
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
            jobGroupAccess.saveJobGroup(group);
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
            if (StringUtils.hasText(jobID)) {
                jobsToAdd.add(new Job(jobID, group));
            }
        }

        jobAccess.saveJobs(jobsToAdd);

        LOGGER.info("Added {} jobs to the queue.", jobsToAdd.size());

        updateAll();
    }

    @Override
    public void removeJobGroup(@NotNull String groupTitle) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null or empty.");
        }
        if (containsGroup(groupTitle)) {
            Collection<Job> toRemove = new ArrayList<>();

            toRemove.addAll(jobAccess.findJobs(groupTitle));
            toRemove.addAll(removeJobs(groupTitle, prioritizedQueue));

            jobAccess.deleteJobs(toRemove);
            JobGroup group = groups.remove(groupTitle);
            groupQueue.remove(group);
            jobGroupAccess.deleteGroup(group);
        }
    }

    private Collection<Job> removeJobs(String groupTitle, Collection<Job> jobList) {
        Collection<Job> toRemove = new ArrayList<>();

        for (Job job : jobList) {
            if (job.getJobGroupTitle().equals(groupTitle)) {
                toRemove.add(job);
            }
        }

        jobList.removeAll(toRemove);

        return toRemove;
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

        for (Job job : jobAccess.findJobs()) {
            if (job.getJobGroupTitle().equals(groupTitle) && job.getJobID().equals(jobID)) {
                Job prioritizedJob = new Job(jobID, groups.get(job.getJobGroupTitle()));
                prioritizedJob.setPrioritized(true);

                prioritizedQueue.add(prioritizedJob);

                jobAccess.saveJob(prioritizedJob);
                jobAccess.deleteJob(job);

                LOGGER.info(prioritizedJob.getJobGroupTitle() + " | " + prioritizedJob.getJobID() + " was prioritized.");

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
        List<Job> jobs = new ArrayList<>(jobAccess.findJobs());

        jobs.sort(new AdvancedSchedulingAlgorithm());

        return jobs;
    }

    /**
     * Gets a sorted list of all jobs that are manually prioritized.
     * @return a list of jobs.
     */
    public List<Job> getPrioritizedQueue() {
        return new ArrayList<>(prioritizedQueue);
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
        for (JobGroup group : groups.values()) {
            group.resetTimeSheet();
            jobGroupAccess.saveJobGroup(group);
        }

        LOGGER.info("Daily job group reset finished.");
    }
}
