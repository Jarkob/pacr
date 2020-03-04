package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.leaderboard_management.Leaderboard;
import pacr.webapp_backend.shared.ILeaderboard;

import static org.junit.jupiter.api.Assertions.*;

public class LeaderboardDashboardModuleTest {

    private static LeaderboardDashboardModule leaderboardModule;

    @BeforeEach
    void init() {
        leaderboardModule = new LeaderboardDashboardModule();
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        final QueueDashboardModule queueModule = new QueueDashboardModule();

        assertNotEquals(leaderboardModule, queueModule);
    }

    @Test
    void equals_SameLeaderboard_ShouldReturnTrue() {
        final LeaderboardDashboardModule otherLeaderboardModule = new LeaderboardDashboardModule();

        final ILeaderboard leaderboard = new Leaderboard();

        leaderboardModule.setLeaderboard(leaderboard);
        otherLeaderboardModule.setLeaderboard(leaderboard);


        assertEquals(leaderboardModule, otherLeaderboardModule);
    }
}
