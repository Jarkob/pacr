package pacr.webapp_backend.scheduler.services;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * A job group is used to track the benchmarking time of a repository.
 */
@Entity
@Getter
@EqualsAndHashCode
public class JobGroup {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Exclude
    @Getter(AccessLevel.NONE)
    private int id;

    private String title;

    @EqualsAndHashCode.Exclude
    private long timeSheet;

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

}
