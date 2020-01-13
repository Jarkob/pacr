package pacr.webapp_backend.dashboard_management.services;


/**
 * Instances of this class represent leaderboard modules on a dashboard.
 * One of these leaderboard modules shows a leaderboard for a specific benchmark.
 *
 * @author Benedikt Hahn
 */
public class LeaderboardDashboardModule extends DashboardModule {
    private String benchmarkName;


    /**
     * Sets the benchmark, this leaderboard is assigned to.
     * @param benchmarkName The name of the new benchmark.
     */
    void setBenchmarkOfLeaderboard(String benchmarkName) {
        this.benchmarkName = benchmarkName;
    }
    @Override
    public boolean equals(Object o) {
        boolean superEquals = super.equals(o);
        if (!superEquals) {
            return false;
        }

        LeaderboardDashboardModule otherModule = (LeaderboardDashboardModule) o;

        if (benchmarkName.equals(otherModule.benchmarkName)) {
            return true;
        }

        return false;
    }
}
