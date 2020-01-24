package pacr.webapp_backend.dashboard_management.services;

import pacr.webapp_backend.dashboard_management.Dashboard;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This interface is used for storing and accessing
 * objects, which are used and managed by the dashboard component.
 *
 * @author Benedikt Hahn
 */
public interface IDashboardAccess {

    /**
     * @param editKey The edit key, with which the dashboard was requested.
     * @return the dashboard with the given edit key.
     */
    Dashboard findByEditKey(@NotNull String editKey);

    /**
     * @param viewKey The view key, with which the dashboard was requested.
     * @return the dashboard with the given view key.
     */
    Dashboard findByViewKey(@NotNull String viewKey);

    /**
     * @param dashboard the dashboard, which will be added to be database.
     */
    void addDashboard(@NotNull Dashboard dashboard);

    /**
     * @param editKey The edit key of the dashboard, which will be deleted.
     * @throws NoSuchElementException if the dashboard does not exist in the database.
     */
    void deleteDashboard(@NotNull String editKey) throws NoSuchElementException;

    /**
     * @param dashboard The dashboard, which will be updated.
     * @throws NoSuchElementException if the dashboard does not exist in the database.
     */
    void updateDashboard(@NotNull Dashboard dashboard) throws NoSuchElementException;

    /**
     * @param key the key, whose type should be returned.
     * @return -1 if no such key exists in the database, 0 if the key is a view key and
     * 1 if the key is an edit key.
     */
    KeyType checkKeyType(String key);

    /**
     * Stores the given deletion interval as the new deletion interval.
     * @param deletionInterval the new deletion interval.
     */
    void setDeletionInterval(long deletionInterval);

    /**
     * Receives the deletion interval from the database.
     * @return the deletion interval.
     */
    long getDeletionInterval();

    /**
     * @return a list of all currently existing dashboards in the database.
     */
    List<Dashboard> getAllDashboards();
}
