package pacr.webapp_backend.scheduler.services;

import java.time.LocalDateTime;
import java.util.Objects;
import pacr.webapp_backend.shared.IJob;

/**
 * A job that is identified by an id and belongs to a job group.
 * A job is queued when it is first created.
 */
public class Job implements IJob {

    private String jobID;
    private LocalDateTime queued;
    private JobGroup group;

    /**
     * Creates a new job and sets its queued date.
     * @param jobID the id of the job.
     * @param group the group of the job.
     */
    Job(String jobID, JobGroup group) {
        this.jobID = jobID;
        this.group = group;
        this.queued = LocalDateTime.now();
    }

    /**
     * @return the current time sheet of the job's job group in seconds.
     */
    long getGroupTimeSheet() {
        return group.getTimeSheet();
    }

    /**
     * @return the date and time the job was queued.
     */
    LocalDateTime getQueued() {
        return queued;
    }

    @Override
    public String getJobID() {
        return jobID;
    }

    @Override
    public String getJobGroupTitle() {
        return group.getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Job job = (Job) o;
        return Objects.equals(jobID, job.jobID) && Objects.equals(group, job.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobID, group);
    }

    @Override
    public String toString() {
        return "Job{ jobID='" + jobID + "'" + ", group=" + group + " }";
    }
}
