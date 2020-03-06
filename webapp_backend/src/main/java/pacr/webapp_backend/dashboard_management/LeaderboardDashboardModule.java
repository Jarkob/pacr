package pacr.webapp_backend.dashboard_management;


import lombok.NoArgsConstructor;
import pacr.webapp_backend.shared.ILeaderboard;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Instances of this class represent leaderboard modules on a dashboard.
 * One of these leaderboard modules shows a leaderboard for a specific benchmark.
 *
 * @author Benedikt Hahn
 */
@Entity
@NoArgsConstructor
public class LeaderboardDashboardModule extends DashboardModule {

    @OneToOne
    private ILeaderboard leaderboard;

    /**
     * Sets the current leaderboard of the dashboard module.
     * @param leaderboard the leaderboard.
     */
    public void setLeaderboard(final ILeaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }

        final LeaderboardDashboardModule otherModule = (LeaderboardDashboardModule) o;

        if (leaderboard == null && otherModule.leaderboard == null) {
            return true;
        }

        if (leaderboard == null) {
            return false;
        }

        return leaderboard.equals(otherModule.leaderboard);
    }
}
