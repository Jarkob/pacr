package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.leaderboard_management.Leaderboard;

import static org.junit.jupiter.api.Assertions.*;

public class LeaderboardDashboardModuleTest {

    private static LeaderboardDashboardModule leaderboardModule;

    @BeforeEach
    void init() {
        leaderboardModule = new LeaderboardDashboardModule();
    }
    @Test
    void setBenchmarkName_SomeName_ShouldSetTheName() {
        final String BENCHMARK_NAME = "someName";
        leaderboardModule.setBenchmarkName(BENCHMARK_NAME);

        assertEquals(BENCHMARK_NAME, leaderboardModule.getBenchmarkName());
    }

    @Test
    void setLeaderboard_UsualLeaderboard_ShouldSetTheLeaderboard() {
        leaderboardModule.setLeaderboard(new Leaderboard());

        assertNotEquals(null, leaderboardModule.getLeaderboard());
    }


    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        QueueDashboardModule queueModule = new QueueDashboardModule();

        assertNotEquals(leaderboardModule, queueModule);
    }
    @Test
    void equals_DifferentBenchmark_ShouldReturnFalse() {
        LeaderboardDashboardModule otherLeaderboardModule = new LeaderboardDashboardModule();

        leaderboardModule.setBenchmarkName("testBenchmark");
        otherLeaderboardModule.setBenchmarkName("otherTestBenchmark");

        assertNotEquals(leaderboardModule, otherLeaderboardModule);
    }

    @Test
    void equals_SameBenchmarks_ShouldReturnTrue() {
        final String BENCHMARK_NAME = "some benchmark";

        LeaderboardDashboardModule otherLeaderboardModule = new LeaderboardDashboardModule();

        leaderboardModule.setBenchmarkName(BENCHMARK_NAME);
        otherLeaderboardModule.setBenchmarkName(BENCHMARK_NAME);

        assertEquals(leaderboardModule, otherLeaderboardModule);
    }
}
