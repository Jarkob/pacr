package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IJobProvider;
import pacr.webapp_backend.shared.IJobScheduler;
import pacr.webapp_backend.shared.IObserver;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

/**
 * Holds a list of jobs and sorts them according to a scheduling policy.
 */
@Component
public class Scheduler implements IJobProvider, IJobScheduler {

    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

    private static final String CRON_DAILY = "0 0 0 * * *";

    private final DynamicPriorityQueue<JobGroup> groupQueue;

    private final Map<String, JobGroup> groups;

    private final Collection<IObserver> observers;

    private final IJobAccess jobAccess;
    private final IJobGroupAccess jobGroupAccess;

    /**
     * Creates a new scheduler and sets the used scheduling policy.
     *
     * @param jobAccess the access interface to store and retrieve jobs.
     * @param jobGroupAccess the access interface to retrieve job groups.
     */
    public Scheduler(final IJobAccess jobAccess, final IJobGroupAccess jobGroupAccess) {
        this.jobAccess = jobAccess;
        this.jobGroupAccess = jobGroupAccess;

        this.groupQueue = new DynamicPriorityQueue<>(new GroupSchedulingAlgorithm());

        this.groups = new HashMap<>();

        this.observers = new ArrayList<>();
    }

    @PostConstruct
    void loadJobsFromStorage() {
        for (final JobGroup group : jobGroupAccess.findAllJobGroups()) {
            groupQueue.add(group);
            groups.put(group.getTitle(), group);
        }
    }

    private JobGroup addGroup(@NotNull final String groupTitle) {
        final JobGroup group;
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

    private boolean containsGroup(@NotNull final String groupTitle) {
        return groups.containsKey(groupTitle);
    }

    private JobGroup getGroup(@NotNull final String groupTitle) {
        return groups.get(groupTitle);
    }

    @Override
    public IJob popJob() {
        Job job = null;

        final Collection<Job> prioritized = jobAccess.findPrioritized();

        if (!prioritized.isEmpty()) {
            job = prioritized.stream().findFirst().orElse(null);
        } else {
            final AdvancedSchedulingAlgorithm advancedSchedulingAlgorithm = new AdvancedSchedulingAlgorithm();
            while (job == null && groupQueue.size() > 0) {
                final JobGroup group = groupQueue.peek();

                final List<Job> jobs = new ArrayList<>(jobAccess.findAllJobs(group.getTitle()));

                if (jobs.isEmpty()) {
                    removeJobGroup(group.getTitle());
                } else {
                    jobs.sort(advancedSchedulingAlgorithm);

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
    public void returnJob(@NotNull final IJob job) {
        Objects.requireNonNull(job, "The returned job cannot be null.");

        LOGGER.info("Job {} | {} was returned to the queue.", job.getJobGroupTitle(), job.getJobID());

        addJob(job.getJobGroupTitle(), job.getJobID());
    }

    /**
     * Adds a new job to the job list with a reference to the given group.
     *
     * @param groupTitle the job group of the job.
     * @param jobID      the id of the job.
     */
    private void addJob(@NotNull String groupTitle, @NotNull String jobID) {
        Collection<String> jobIDs = List.of(jobID);

        addJobs(groupTitle, jobIDs);
    }

    @Override
    public void addToGroupTimeSheet(@NotNull final String groupTitle, final long time) {
        if (containsGroup(groupTitle)) {
            final JobGroup group = getGroup(groupTitle);

            group.addToTimeSheet(time);
            jobGroupAccess.saveJobGroup(group);
        }
    }

    @Override
    public void addJobs(@NotNull String groupTitle, @NotNull Collection<String> jobIDs) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null or empty.");
        }
        Objects.requireNonNull(jobIDs, "The jobIds cannot be null.");

        Set<String> currentJobs = jobAccess.findAllJobs(groupTitle).stream()
                                                                    .map(Job::getJobID)
                                                                    .collect(Collectors.toSet());

        Set<Job> jobsToAdd = new HashSet<>();
        JobGroup group = addGroup(groupTitle);

        for (String jobID : jobIDs) {
            if (StringUtils.hasText(jobID) && !currentJobs.contains(jobID)) {
                jobsToAdd.add(new Job(jobID, group));
            }
        }

        jobAccess.saveJobs(jobsToAdd);

        int amtDuplicates = jobIDs.size() - jobsToAdd.size();
        LOGGER.info("Added {} {} to the queue. Skipping {} {}.",
                jobsToAdd.size(), jobsToAdd.size() == 1 ? "job" : "jobs",
                amtDuplicates, amtDuplicates == 1 ? "duplicate" : "duplicates");

        updateAll();
    }

    @Override
    public void removeJobGroup(@NotNull final String groupTitle) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null or empty.");
        }
        if (containsGroup(groupTitle)) {
            final Collection<Job> toRemove = new ArrayList<>(jobAccess.findAllJobs(groupTitle));

            jobAccess.deleteJobs(toRemove);
            final JobGroup group = groups.remove(groupTitle);
            groupQueue.remove(group);
            jobGroupAccess.deleteGroup(group);
        }
    }

    @Override
    public void removeJobs(@NotNull String groupTitle, @NotNull Set<String> jobIDs) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null or empty.");
        }
        Objects.requireNonNull(jobIDs, "The jobIDs cannot be null.");

        if (containsGroup(groupTitle)) {
            Collection<Job> toRemove = new ArrayList<>(jobAccess.findAllJobs(groupTitle));
            toRemove.removeIf(job -> !jobIDs.contains(job.getJobID()));

            jobAccess.deleteJobs(toRemove);
        }
    }

    /**
     * Moves a job to the manually prioritized jobs queue.
     * @param groupTitle the title of the job.
     * @param jobID the id of the job.
     * @return if the job was successfully prioritized.
     */
    public boolean givePriorityTo(@NotNull final String groupTitle, @NotNull final String jobID) {
        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null.");
        }

        if (!StringUtils.hasText(jobID)) {
            throw new IllegalArgumentException("The jobID cannot be null.");
        }

        if (containsGroup(groupTitle)) {
            Job toPrioritize = findJobToPrioritize(groupTitle, jobID);

            if (toPrioritize == null) {
                return false;
            }

            JobGroup group = groups.get(toPrioritize.getJobGroupTitle());

            Job prioritizedJob = new Job(jobID, group);
            prioritizedJob.setPrioritized(true);

            jobAccess.deleteJob(toPrioritize);
            jobAccess.saveJob(prioritizedJob);

            LOGGER.info("'{}' | '{}' was prioritized.",
                    prioritizedJob.getJobGroupTitle(), prioritizedJob.getJobID());

            return true;
        }

        return false;
    }

    private Job findJobToPrioritize(String groupTitle, String jobID) {
        for (Job job : jobAccess.findJobs()) {
            if (job.getJobGroupTitle().equals(groupTitle) && job.getJobID().equals(jobID)) {
                return job;
            }
        }

        return null;
    }

    /**
     * Gets a sorted list of all jobs that are not manually prioritized.
     * @param pageable contains paging information.
     * @return a page of jobs.
     */
    public Page<Job> getJobsQueue(final Pageable pageable) {
        final Page<Job> page = jobAccess.findJobs(pageable);
        final List<Job> jobs = new ArrayList<>(page.getContent());

        jobs.sort(new AdvancedSchedulingAlgorithm());

        return new PageImpl<>(jobs, page.getPageable(), page.getTotalElements());
    }

    /**
     * Gets a sorted list of all jobs that are manually prioritized.
     * @param pageable contains paging information.
     * @return a page of jobs.
     */
    public Page<Job> getPrioritizedQueue(final Pageable pageable) {
        return jobAccess.findPrioritized(pageable);
    }

    @Override
    public void subscribe(@NotNull final IObserver observer) {
        Objects.requireNonNull(observer);

        observers.add(observer);
    }

    @Override
    public void unsubscribe(@NotNull final IObserver observer) {
        Objects.requireNonNull(observer);

        observers.remove(observer);
    }

    @Override
    public void updateAll() {
        for (final IObserver observer : observers) {
            observer.update();
        }
    }

    /**
     * Resets the groups field daily.
     */
    @Scheduled(cron = CRON_DAILY)
    void resetJobGroupTimeSheets() {
        for (final JobGroup group : groups.values()) {
            group.resetTimeSheet();
            jobGroupAccess.saveJobGroup(group);
        }

        LOGGER.info("Daily job group reset finished.");
    }
}
