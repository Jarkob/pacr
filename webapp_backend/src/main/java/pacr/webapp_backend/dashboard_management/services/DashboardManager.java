package pacr.webapp_backend.dashboard_management.services;

import javassist.NotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.LeaderboardDashboardModule;
import pacr.webapp_backend.shared.ILeaderboardGetter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the entry point into the services of
 * the dashboard management package. Therefore it contains
 * methods for managing requests and delegating them to other parts of the component.
 *
 * @author Benedikt Hahn
 */
@Component
public class DashboardManager {

    private static final String CRON_DAILY = "0 0 0 * * *";

    ILeaderboardGetter leaderboardGetter;

    /**
     * Creates a new dashboard manager.
     */
    public DashboardManager() {

    }

    /**
     * @param key the key of the dashboard which gets requested.
     * @return the requested dashboard
     * @throws NotFoundException if the key does not belong to a dashboard.
     */
    public Dashboard getDashboard(String key) throws NotFoundException {
        Dashboard dashboard = DatabaseTalker.getDashboard(key);
        dashboard.updateLastAccess();

        //To prevent access from unauthorized users
        dashboard.setEditKey(null);

        //Store the leaderboards in leaderboard modules.
        for (LeaderboardDashboardModule ldm : dashboard.getLeaderboardModules()) {
            String benchmarkName = ldm.getBenchmarkName();
            ldm.setLeaderboard(leaderboardGetter.getLeaderboard(benchmarkName));
        }

        return dashboard;
    }


    /**
     * @param dashboard The dashboard to be added to the database.
     * @return The keys of this dashboard in a list. The first item is the view key,
     * the second the edit key.
     */
    public List<String> addDashboard(Dashboard dashboard) {
        KeyManager.generateEditKey(dashboard);
        KeyManager.generateViewKey(dashboard);

        dashboard.getLeaderboardModules().forEach(LeaderboardDashboardModule::deleteLeaderboard);

        DatabaseTalker.storeDashboard(dashboard);

        return Arrays.asList(dashboard.getViewKey(), dashboard.getEditKey());
    }

    /**
     * Saves an updated version of an existing dashboard to the database
     * @param dashboard the updated dashboard.
     */
    public void updateDashboard(Dashboard dashboard) {
        dashboard.getLeaderboardModules().forEach(LeaderboardDashboardModule::deleteLeaderboard);
        dashboard.updateLastAccess();
        DatabaseTalker.updateDashboard(dashboard);
    }

    /**@param interval the new deletion interval in days.
     */
    public void setDeletionInterval(long interval) {
        DatabaseTalker.setDeletionInterval(interval);
    }


    /**
     * @return the deletion interval stored in the database.
     */
    public long getDeletionInterval() {
        return DatabaseTalker.getDeletionInterval();
    }

    /**
     * Deletes the dashboard with the specified key from the database.
     * @param key the key of the dashboard with which the delete operation was initiated.
     * @throws NotFoundException if the key does not exist.
     * @throws IllegalAccessException if the key is a view key and not sufficient to allow the deletion of a dashboard.
     */
    public void deleteDashboard(String key) throws NotFoundException, IllegalAccessException {
        DatabaseTalker.deleteDashboard(key);
    }

    /**
     * Deletes all dashboards, which are older than the deletion interval.
     */
    @Scheduled(cron = CRON_DAILY)
    private void deleteOldDashboards() {

        List<Dashboard> dashboards = DatabaseTalker.getAllDashboards();
        long deletionInterval = getDeletionInterval();

        for (Dashboard dashboard : dashboards) {
            LocalDate now = LocalDate.now();
            LocalDate lastRetrieved = dashboard.getLastAccess();

            Period interval = Period.between(lastRetrieved, now);

            if (deletionInterval > interval.getDays()) {
                try {
                    deleteDashboard(dashboard.getEditKey());
                } catch (NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
