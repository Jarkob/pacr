package pacr.webapp_backend.scheduler.services;

/**
 * A job group is used to track the benchmarking time of a repository.
 */
class JobGroup {

    private String title;
    private long benchmarkingTime;

    /**
     * Creates a new job group.
     * @param title the title of the job group (cannot be null or empty).
     */
    JobGroup(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }

        this.title = title;
        this.benchmarkingTime = 0;
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
    long getBenchmarkingTime() {
        return benchmarkingTime;
    }

    /**
     * Adds the given time to the group's time sheet.
     * @param time the time in seconds. (>= 0)
     */
    void updateTimeSheet(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time cannot be less than zero.");
        }

        this.benchmarkingTime += time;
    }

    @Override
    public String toString() {
        return "JobGroup{ " + "title='" + title + "'" + ", benchmarkingTime=" + benchmarkingTime + " }";
    }
}
