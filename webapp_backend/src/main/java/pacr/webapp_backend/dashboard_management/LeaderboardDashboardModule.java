package pacr.webapp_backend.dashboard_management;


import pacr.webapp_backend.shared.ILeaderboard;

import javax.persistence.Entity;

/**
 * Instances of this class represent leaderboard modules on a dashboard.
 * One of these leaderboard modules shows a leaderboard for a specific benchmark.
 *
 * @author Benedikt Hahn
 */
@Entity
public class LeaderboardDashboardModule extends DashboardModule {
    private String benchmarkName;

    private transient ILeaderboard leaderboard;


    /**
     * @return the name of the benchmark tracked in this leaderboard.
     */
    public String getBenchmarkName() {
        return this.benchmarkName;
    }

    /**
     * Sets the leaderboard of this benchmark to the given leaderboard.
     * @param leaderboard the leaderboard of this module.
     */
    public void setLeaderboard(ILeaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    /**
     * Deletes the leaderboard of this module to avoid duplicate data in the database,
     * when storing this leaderboard.
     */
    public void deleteLeaderboard() {
        this.leaderboard = null;
    }

    /**
     * Sets the benchmark, this leaderboard is assigned to.
     * @param benchmarkName The name of the new benchmark.
     */
    public void setBenchmarkOfLeaderboard(String benchmarkName) {
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
