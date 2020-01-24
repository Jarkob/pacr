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
     * Public no argument constructor for jpa.
     */
    public LeaderboardDashboardModule() {

    }

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
     * The leaderboard of this benchmark
     * @return
     */
    ILeaderboard getLeaderboard() {
        return this.leaderboard;
    }

    /**
     * Sets the benchmark, this leaderboard is assigned to.
     * @param benchmarkName The name of the new benchmark.
     */
    public void setBenchmarkName(String benchmarkName) {
        this.benchmarkName = benchmarkName;
    }


    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        LeaderboardDashboardModule otherModule = (LeaderboardDashboardModule) o;

        return benchmarkName.equals(otherModule.benchmarkName);
    }
}
