package pacr.webapp_backend.dashboard_management.services;

import javassist.NotFoundException;
import pacr.webapp_backend.dashboard_management.Dashboard;

import javax.validation.constraints.NotNull;

/**
 * This interface is used for storing and accessing
 * objects, which are used and managed by the dashboard component.
 *
 * @author Benedikt Hahn
 */
public interface IDashboardAccess {

    /**
     * @param key The key, with which the dashboard was requested.
     * @return the dashboard with the given view key.
     * @throws NotFoundException if the dashboard could not be found.
     */
    Dashboard getDashboard(@NotNull String key) throws NotFoundException;

    /**
     * @param dashboard the dashboard, which will be added to be database.
     */
    void addDashboard(@NotNull Dashboard dashboard);

    /**
     * @param editKey The edit key of the dashboard, which will be deleted.
     * @throws NotFoundException if the dashboard does not exist in the database.
     */
    void deleteDashboard(@NotNull String editKey) throws NotFoundException;

    /**
     * @param id The idkey of the dashboard, which will be deleted.
     * @throws NotFoundException if the dashboard does not exist in the database.
     */
    void deleteDashboard(@NotNull int id) throws NotFoundException;

    /**
     * @param key the key, whose type should be returned.
     * @return -1 if no such key exists in the database, 0 if the key is a view key and
     * 1 if the key is an edit key.
     */
    int checkKeyType(String key);

    /**
     * Stores the given deletion interval as the new deletion interval.
     * @param deletionInterval the new deletion interval.
     */
    void setDeletionInterval(int deletionInterval);

    /**
     * Receives the deletion interval from the database.
     * @return the deletion interval.
     */
    int getDeletionInterval();
}
