package pacr.webapp_backend.dashboard_management.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.KeysAlreadyInitializedException;
import pacr.webapp_backend.dashboard_management.LeaderboardDashboardModule;
import pacr.webapp_backend.shared.ILeaderboardGetter;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * This class is a facade for the dashboard management package. Therefore it contains
 * methods for managing requests and delegating them to other parts of the component.
 *
 * @author Benedikt Hahn
 */
@Component
public class DashboardManager {
    private static final Logger LOGGER = LogManager.getLogger(DashboardManager.class);

    private static final String CRON_DAILY = "0 0 0 * * *";

    ILeaderboardGetter leaderboardGetter;

    DatabaseTalker databaseTalker;

    /**
     * Creates a new dashboard manager.
     */
    public DashboardManager() {

    }

    /**
     * @param key the key, with which the request is made.
     * @return the requested dashboard
     * @throws NoSuchElementException if the key does not belong to a dashboard.
     */
    public Dashboard getDashboard(String key) throws NoSuchElementException {
        Dashboard dashboard = databaseTalker.getDashboard(key);
        dashboard.updateLastAccess();

        return dashboard;
    }


    /**
     * @param dashboard The dashboard to be added to the database.
     * @return The keys of this dashboard in a pair. The first item is the view key,
     * the second the edit key.
     */
    public Pair<String, String> addDashboard(Dashboard dashboard) {

        try {
            dashboard.initializeKeys(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        } catch (KeysAlreadyInitializedException e) {
            LOGGER.error("The dashboard " + dashboard.getTitle() + " is already initialized and cannot "
                    + "be initialized again.");
        }

        databaseTalker.storeDashboard(dashboard);

        return Pair.of(dashboard.getViewKey(), dashboard.getEditKey());
    }

    /**
     * Saves an updated version of an existing dashboard to the database
     * @param dashboard the updated dashboard.
     */
    public void updateDashboard(Dashboard dashboard) {
        dashboard.updateLastAccess();
        databaseTalker.updateDashboard(dashboard);
    }

    /**
     * @param interval the new deletion interval in days.
     */
    public void setDeletionInterval(long interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("The deletion interval must last at least one day long.");
        }
        databaseTalker.setDeletionInterval(interval);
    }


    /**
     * @return the deletion interval stored in the database.
     */
    public long getDeletionInterval() {
        return databaseTalker.getDeletionInterval();
    }

    /**
     * Deletes the dashboard with the specified key from the database.
     * @param key the key of the dashboard with which the delete operation was initiated.
     * @throws NoSuchElementException if the key does not exist.
     * @throws IllegalAccessException if the key is a view key and not sufficient to allow the deletion of a dashboard.
     */
    public void deleteDashboard(String key) throws NoSuchElementException, IllegalAccessException {
        databaseTalker.deleteDashboard(key);
    }

    /**
     * Deletes all dashboards, which are older than the deletion interval.
     */
    @Scheduled(cron = CRON_DAILY)
    private void deleteOldDashboards() {

        Collection<Dashboard> dashboards = databaseTalker.getAllDashboards();
        long deletionInterval = getDeletionInterval();

        for (Dashboard dashboard : dashboards) {
            LocalDate now = LocalDate.now();
            LocalDate lastRetrieved = dashboard.getLastAccess();

            Period interval = Period.between(lastRetrieved, now);

            if (deletionInterval > interval.getDays()) {
                try {
                    deleteDashboard(dashboard.getEditKey());
                } catch (NoSuchElementException | IllegalAccessException e) {
                    LOGGER.warn("The dashboard " + dashboard.getTitle() + "could not be delete, even though it is"
                            + "older than the current deletion interval.");
                }
            }
        }

    }

}
