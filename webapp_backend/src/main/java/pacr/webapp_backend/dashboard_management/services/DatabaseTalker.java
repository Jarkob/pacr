package pacr.webapp_backend.dashboard_management.services;

import org.springframework.stereotype.Service;
import pacr.webapp_backend.dashboard_management.Dashboard;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Communicates with the database and has methods for tasks,
 * where database access is required.
 */
@Service
public class DatabaseTalker {

    IDashboardAccess dashboardAccess;

    IDeletionIntervalAccess deletionIntervalAccess;

    static final long DEFAULT_DELETION_INTERVAL = 10L;

    /**
     * Creates a new database talker.
     * @param dashboardAccess The object, implementing {@link IDashboardAccess}, which allows access to dashboards in
     *                        the database.
     * @param deletionIntervalAccess The object, implementing {@link IDeletionIntervalAccess}, which allows access
     *                               to dashboards in the database.
     */
    public DatabaseTalker(@NotNull IDashboardAccess dashboardAccess,
                          @NotNull IDeletionIntervalAccess deletionIntervalAccess) {
        Objects.requireNonNull(dashboardAccess, "The dashboard access object must not be null.");
        Objects.requireNonNull(deletionIntervalAccess, "The deletion interval access object must not be null");

        this.dashboardAccess = dashboardAccess;
        this.deletionIntervalAccess = deletionIntervalAccess;
    }

    /**
     * Checks what kind of key the given string is.
     *
     * @param key The string which should be checked.
     * @return -1, if the key does not belong to any dashboard, 0, if the key is a view key
     * and 1 if the key is an edit key.
     */
    private KeyType checkKeyType(String key) {

        boolean isEditKey = dashboardAccess.existsDashboardByEditKey(key);
        boolean isViewKey = dashboardAccess.existsDashboardByViewKey(key);

        if (isEditKey && !isViewKey) {
            return KeyType.EDIT_KEY;
        } else if (!isEditKey && isViewKey) {
            return KeyType.VIEW_KEY;
        } else if (!isEditKey) {
            return KeyType.INVALID_KEY;
        } else {
            throw new IllegalStateException("The key '" + key + "' is both a edit and a view key.");
        }
    }

    /**
     * Stores the given dashboard in the database.
     *
     * @param dashboard the dashboard which will be stored.
     */
    void storeDashboard(Dashboard dashboard) {
        dashboardAccess.storeDashboard(dashboard);
    }

    /**
     * Updates an existing dashboard in the database.
     *
     * @param dashboard the newer version of the dashboard.
     * @throws IllegalAccessException if the dashboard does not contain a valid edit key, for access.
     * @throws NoSuchElementException if the dashboard does not yet exist.
     */
    void updateDashboard(Dashboard dashboard) throws IllegalAccessException, NoSuchElementException {
        String editKey = dashboard.getEditKey();
        if (editKey == null) {
            throw new IllegalAccessException("The dashboard can only be updated via access with an edit key.");
        }

        KeyType keyType = checkKeyType(editKey);
        if (keyType == KeyType.INVALID_KEY) {
            throw new NoSuchElementException("The key ' " + editKey + "' does not belong to an existing dashboard.");
        } else if (keyType == KeyType.VIEW_KEY) {
            throw new IllegalAccessException("The supposed edit key '" + editKey + "' is not an edit key.");
        }

        dashboardAccess.storeDashboard(dashboard);
    }

    /**
     * Deletes a dashboard with a specific edit key from the database
     *
     * @param key The key, with which the delete operation was initiated.
     * @throws NoSuchElementException if the key does not exist.
     * @throws IllegalAccessException if the key is a view key and not sufficient to allow the deletion of a dashboard.
     */
    void deleteDashboard(String key) throws NoSuchElementException, IllegalAccessException {
        KeyType keyType = checkKeyType(key);
        if (keyType == KeyType.INVALID_KEY) {
            throw new NoSuchElementException("The key '" + key + "' does not belong to an existing dashboard.");
        } else if (keyType == KeyType.VIEW_KEY) {
            throw new IllegalAccessException("The key '" + key + "' is not an edit key.");
        }

        dashboardAccess.removeDashboardByEditKey(key);

    }

    /**
     * Stores the given deletion interval as the new deletion interval.
     *
     * @param deletionInterval the new deletion interval.
     */
    void setDeletionInterval(long deletionInterval) {

        deletionIntervalAccess.setDeletionInterval(deletionInterval);
    }

    /**
     * Returns the deletion interval and initializes it, if it is not stored yet.
     * @return the deletion interval from the database.
     */
    long getDeletionInterval() {
        try {
            return deletionIntervalAccess.getDeletionInterval();
        } catch (NoSuchElementException e) {
            setDeletionInterval(DEFAULT_DELETION_INTERVAL);
            return DEFAULT_DELETION_INTERVAL;
        }
    }

    /**
     * @param key the key of the dashboard which gets requested.
     * @return the requested dashboard
     * @throws NoSuchElementException if the key does not belong to a dashboard.
     */
    Dashboard getDashboard(String key) throws NoSuchElementException {
        KeyType keyType = checkKeyType(key);
        if (keyType == KeyType.INVALID_KEY) {
            throw new NoSuchElementException("The key '" + key + "' does not belong to an existing dashboard.");
        } else {
            Dashboard dashboard = keyType == KeyType.VIEW_KEY ? dashboardAccess.findByViewKey(key)
                    : dashboardAccess.findByEditKey(key);
            if (keyType == KeyType.VIEW_KEY) {
                dashboard.prepareForViewAccess();
            }
            return dashboard;
        }
    }

    /**
     * @return a list of all dashboards.
     */
    Collection<Dashboard> getAllDashboards() {
        return dashboardAccess.findAll();
    }
}
