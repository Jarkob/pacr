package pacr.webapp_backend.dashboard_management.services;

import java.util.NoSuchElementException;

/**
 * Gives access to the deletion interval in the database.
 * This interface contains methods for getting and setting the deletion interval.
 */
public interface IDeletionIntervalAccess {

    /**
     * Stores the given deletion interval in days as the new deletion interval.
     *
     * @param deletionInterval the new deletion interval.
     */
    void setDeletionInterval(long deletionInterval);

    /**
     * Returns the deletion interval in days from the database.
     *
     * @return the deletion interval.
     * @throws NoSuchElementException if the deletion interval has not been saved yet.
     */
    long getDeletionInterval() throws NoSuchElementException;

    /**
     * Deletes the current deletion interval.
     */
    void delete();
}
