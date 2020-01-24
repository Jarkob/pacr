package pacr.webapp_backend.dashboard_management.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pacr.webapp_backend.dashboard_management.Dashboard;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Communicates with the database and has methods for tasks,
 * where database access is required.
 */
public class DatabaseTalker {

    private static final Logger LOGGER = LogManager.getLogger(DashboardManager.class);

    IDashboardAccess dashboardAccess;

    /**
     * Empty constructor for jpa.
     */
    public DatabaseTalker() {

    }

    /**
     * Checks what kind of key the given string is.
     * @param key The string which should be checked.
     * @return -1, if the key does not belong to any dashboard, 0, if the key is a view key
     * and 1 if the key is an edit key.
     */
    private KeyType checkKeyType(String key) {
        return dashboardAccess.checkKeyType(key);
    }

    /**
     * Stores the given dashboard in the database.
     * @param dashboard the dashboard which will be stored.
     */
    void storeDashboard(Dashboard dashboard) {
        dashboardAccess.addDashboard(dashboard);
    }

    /**
     * Updates an existing dashboard in the database.
     * @param dashboard the newer version of the dashboard.
     */
    void updateDashboard(Dashboard dashboard) {
        try {
            dashboardAccess.updateDashboard(dashboard);
        } catch (NoSuchElementException e) {
            LOGGER.warn("The dashboard " + dashboard.getTitle() + " does not yet exist in the database.");
        }
    }

    /**
     * Deletes a dashboard with a specific edit key from the database
     * @param key The key, with which the delete operation was initiated.
     * @throws NoSuchElementException if the key does not exist.
     * @throws IllegalAccessException if the key is a view key and not sufficient to allow the deletion of a dashboard.
     */
    void deleteDashboard(String key) throws NoSuchElementException, IllegalAccessException {
        KeyType keyType = checkKeyType(key);
        if (keyType == KeyType.INVALID_KEY) {
            throw new NoSuchElementException("The key " + key + "does not belong to an existing dashboard.");
        } else  if (keyType == KeyType.VIEW_KEY) {
            throw new IllegalAccessException("The key " + key + "is not an edit key.");
        }

        dashboardAccess.deleteDashboard(key);
    }

    /**
     * Stores the given deletion interval as the new deletion interval.
     * @param deletionInterval the new deletion interval.
     */
    void setDeletionInterval(long deletionInterval) {
        dashboardAccess.setDeletionInterval(deletionInterval);
    }

    /**
     * @return the deletion interval from the database.
     */
    long getDeletionInterval() {
        return dashboardAccess.getDeletionInterval();
    }

    /**
     * @param key the key of the dashboard which gets requested.
     * @return the requested dashboard
     * @throws NoSuchElementException if the key does not belong to a dashboard.
     */
    Dashboard getDashboard(String key) throws NoSuchElementException {
        KeyType keyType = checkKeyType(key);
        if (keyType == KeyType.INVALID_KEY) {
            throw new NoSuchElementException("The key " + key + "does not belong to an existing dashboard.");
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
        return dashboardAccess.getAllDashboards();
    }
}
