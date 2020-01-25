package pacr.webapp_backend.dashboard_management.services;

import org.springframework.stereotype.Service;
import pacr.webapp_backend.dashboard_management.Dashboard;

import javax.validation.constraints.NotNull;
import java.util.Collection;

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
     * Stores the given dashboard in the database, overwriting an older version, if necessary.
     *
     * @param dashboard the dashboard, which will be stored in the database.
     */
    void storeDashboard(@NotNull Dashboard dashboard);

    /**
     * @param editKey The edit key of the dashboard, which will be deleted.
     * @return the removed dashboard.
     */
    Dashboard removeDashboardByEditKey(String editKey);

    /**
     * @param editKey the edit key, whose existence should be checked.
     * @return {@code true} if the edit key does belong to an existing dashboard, else {@code false}.
     */
    boolean existsDashboardByEditKey(String editKey);

    /**
     * @param viewKey the view key, whose existence should be checked.
     * @return {@code true} if the view key does belong to an existing dashboard, else {@code false}.
     */
    boolean existsDashboardByViewKey(String viewKey);

    /**
     * @return a collection of all dashboards, that currently exist in the database.
     */
    Collection<Dashboard> findAll();
}
