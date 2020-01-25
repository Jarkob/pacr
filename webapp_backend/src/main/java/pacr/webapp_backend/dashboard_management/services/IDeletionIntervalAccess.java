package pacr.webapp_backend.dashboard_management.services;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Gives access to the deletion interval in the database.
 * This interface contains methods for getting and setting the deletion interval.
 */
public interface IDeletionIntervalAccess {

    /**
     * Stores the given deletion interval as the new deletion interval.
     *
     * @param deletionInterval the new deletion interval.
     */
    void setDeletionInterval(long deletionInterval);

    /**
     * Returns the deletion interval from the database.
     *
     * @return the deletion interval.
     * @throws NoSuchElementException if the deletion interval has not been saved yet.
     */
    long getDeletionInterval() throws NoSuchElementException;

}
