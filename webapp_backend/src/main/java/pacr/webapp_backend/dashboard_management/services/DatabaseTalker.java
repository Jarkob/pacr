package pacr.webapp_backend.dashboard_management.services;

import javassist.NotFoundException;
import pacr.webapp_backend.dashboard_management.Dashboard;

import java.util.List;

/**
 * Communicates with the database and has methods for tasks,
 * where database access is required.
 */
public class DatabaseTalker {

    static IDashboardAccess dashboardAccess;


    /**
     * Checks what kind of key the given string is.
     * @param key The string which should be checked.
     * @return -1, if the key does not belong to any dashboard, 0, if the key is a view key
     * and 1 if the key is an edit key.
     */
    private static int checkKeyType(String key) {
        return dashboardAccess.checkKeyType(key);
    }

    /**
     * Stores the given dashboard in the database.
     * @param dashboard the dashboard which will be stored.
     */
    static void storeDashboard(Dashboard dashboard) {
        dashboardAccess.addDashboard(dashboard);
    }

    /**
     * Updates an existing dashboard in the database.
     * @param dashboard the newer version of the dashboard.
     */
    static void updateDashboard(Dashboard dashboard) {
        try {
            dashboardAccess.deleteDashboard(dashboard.getId());
            dashboardAccess.addDashboard(dashboard);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a dashboard with a specific edit key from the database
     * @param key The key, with which the delete operation was initiated.
     * @throws NotFoundException if the key does not exist.
     * @throws IllegalAccessException if the key is a view key and not sufficient to allow the deletion of a dashboard.
     */
    static void deleteDashboard(String key) throws NotFoundException, IllegalAccessException {
        int keyType = checkKeyType(key);
        if (keyType == -1) {
            throw new NotFoundException("The key " + key + "does not belong to an existing dashboard.");
        } else  if (keyType == 0) {
            throw new IllegalAccessException("The key " + key + "is not an edit key.");
        }

        dashboardAccess.deleteDashboard(key);
    }

    /**
     * Stores the given deletion interval as the new deletion interval.
     * @param deletionInterval the new deletion interval.
     */
    static void setDeletionInterval(long deletionInterval) {
        dashboardAccess.setDeletionInterval(deletionInterval);
    }

    /**
     * @return the deletion interval from the database.
     */
    static long getDeletionInterval() {
        return dashboardAccess.getDeletionInterval();
    }

    /**
     * @param key the key of the dashboard which gets requested.
     * @return the requested dashboard
     * @throws NotFoundException if the key does not belong to a dashboard.
     */
    static Dashboard getDashboard(String key) throws NotFoundException {
        int keyType = checkKeyType(key);
        if (keyType == -1) {
            throw new NotFoundException("The key " + key + "does not belong to an existing dashboard.");
        } else {
            return dashboardAccess.getDashboard(key);
        }
    }

    /**
     * @return a list of all dashboards.
     */
    static List<Dashboard> getAllDashboards() {
        return dashboardAccess.getAllDashboards();
    }
}
