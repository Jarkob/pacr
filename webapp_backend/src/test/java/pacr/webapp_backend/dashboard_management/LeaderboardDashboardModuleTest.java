package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.leaderboard_management.Leaderboard;

import static org.junit.jupiter.api.Assertions.*;

public class LeaderboardDashboardModuleTest {

    private static LeaderboardDashboardModule leaderboardModule;

    @BeforeEach
    void init() {
        leaderboardModule = new LeaderboardDashboardModule(12);
    }

    @Test
    void constructor_NoArguments_ShouldHaveInvalidState() {
        LeaderboardDashboardModule leadModule = new LeaderboardDashboardModule();

        assertThrows(IllegalStateException.class, leadModule::getPosition);
    }

    @Test
    void setBenchmarkOfLeaderboard_SomeName_ShouldSetTheName() {
        final String BENCHMARK_NAME = "someName";
        leaderboardModule.setBenchmarkOfLeaderboard(BENCHMARK_NAME);

        assertEquals(BENCHMARK_NAME, leaderboardModule.getBenchmarkName());
    }

    @Test
    void setLeaderboard_UsualLeaderboard_ShouldSetTheLeaderboard() {
        leaderboardModule.setLeaderboard(new Leaderboard());

        assertNotEquals(null, leaderboardModule.getLeaderboard());
    }

    @Test
    void deleteLeaderboard_ShouldHaveNullLeaderboard() {
        leaderboardModule.setLeaderboard(new Leaderboard());
        leaderboardModule.deleteLeaderboard();

        assertEquals(null, leaderboardModule.getLeaderboard());
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        QueueDashboardModule queueModule = new QueueDashboardModule(12);

        assertNotEquals(leaderboardModule, queueModule);
    }

    @Test
    void equals_DifferentPosition_ShouldReturnFalse() {
        LeaderboardDashboardModule otherLeaderboardModule = new LeaderboardDashboardModule(2);

        assertNotEquals(leaderboardModule, otherLeaderboardModule);
    }

    @Test
    void equals_DifferentBenchmark_ShouldReturnFalse() {
        LeaderboardDashboardModule otherLeaderboardModule = new LeaderboardDashboardModule(12);

        leaderboardModule.setBenchmarkOfLeaderboard("testBenchmark");
        otherLeaderboardModule.setBenchmarkOfLeaderboard("otherTestBenchmark");

        assertNotEquals(leaderboardModule, otherLeaderboardModule);
    }

    @Test
    void equals_SameBenchmarks_ShouldReturnTrue() {
        final String BENCHMARK_NAME = "some benchmark";

        LeaderboardDashboardModule otherLeaderboardModule = new LeaderboardDashboardModule(12);

        leaderboardModule.setBenchmarkOfLeaderboard(BENCHMARK_NAME);
        otherLeaderboardModule.setBenchmarkOfLeaderboard(BENCHMARK_NAME);

        assertEquals(leaderboardModule, otherLeaderboardModule);
    }
}
