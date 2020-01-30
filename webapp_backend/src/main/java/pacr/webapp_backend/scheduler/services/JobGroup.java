package pacr.webapp_backend.scheduler.services;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

/**
 * A job group is used to track the benchmarking time of a repository.
 */
@Entity
public class JobGroup {

    @Id
    @GeneratedValue
    private int id;

    private String title;
    private transient long timeSheet;

    /**
     * Creates an empty JobGroup.
     *
     * Needed for JPA.
     */
    public JobGroup() {
    }

    /**
     * Creates a new job group.
     * @param title the title of the job group (cannot be null or empty).
     */
    JobGroup(@NotNull String title) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }

        this.title = title;
        this.timeSheet = 0;
    }

    /**
     * @return the title of the group.
     */
    String getTitle() {
        return title;
    }

    /**
     * @return the current time sheet of the group in seconds.
     */
    long getTimeSheet() {
        return timeSheet;
    }

    /**
     * Adds the given time to the group's time sheet.
     * @param time the time in seconds. (>= 0)
     */
    void addToTimeSheet(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time cannot be less than zero.");
        }

        this.timeSheet += time;
    }

    /**
     * Sets the benchmarking time to 0.
     */
    void resetTimeSheet() {
        this.timeSheet = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JobGroup jobGroup = (JobGroup) o;

        return Objects.equals(title, jobGroup.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "JobGroup{ " + "title='" + title + "'" + ", benchmarkingTime=" + timeSheet + " }";
    }
}
