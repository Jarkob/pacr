package pacr.webapp_backend.git_tracking.services;

/**
 * Represents the interface for storing and accessing
 * the pull interval.
 *
 * @author Pavel Zwerschke
 */
public interface IPullIntervalAccess {
    /**
     * Gets the pull interval.
     * @return pull interval.
     */
    int getPullInterval();

    /**
     * Sets the pull interval.
     * @param interval is the pull interval.
     */
    void setPullInterval(int interval);

}
