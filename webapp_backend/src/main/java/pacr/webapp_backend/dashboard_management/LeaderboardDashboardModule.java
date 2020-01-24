package pacr.webapp_backend.dashboard_management;


import pacr.webapp_backend.shared.ILeaderboard;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Instances of this class represent leaderboard modules on a dashboard.
 * One of these leaderboard modules shows a leaderboard for a specific benchmark.
 *
 * @author Benedikt Hahn
 */
@Entity
public class LeaderboardDashboardModule extends DashboardModule {

    @OneToOne
    private ILeaderboard leaderboard;

    /**
     * Public no argument constructor for jpa.
     */
    public LeaderboardDashboardModule() {

    }

    public void setLeaderboard(ILeaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        LeaderboardDashboardModule otherModule = (LeaderboardDashboardModule) o;

        return leaderboard.equals(otherModule.leaderboard);
    }
}
