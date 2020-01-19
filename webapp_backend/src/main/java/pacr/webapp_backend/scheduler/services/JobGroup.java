package pacr.webapp_backend.scheduler.services;

import javax.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

/**
 * A job group is used to track the benchmarking time of a repository.
 */
class JobGroup {

    private String title;
    private long timeSheet;

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
    public String toString() {
        return "JobGroup{ " + "title='" + title + "'" + ", benchmarkingTime=" + timeSheet + " }";
    }
}
