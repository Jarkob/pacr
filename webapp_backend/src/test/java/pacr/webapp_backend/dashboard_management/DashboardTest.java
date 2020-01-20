package pacr.webapp_backend.dashboard_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Commit;
import pacr.webapp_backend.leaderboard_management.Leaderboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class DashboardTest {


    public static Dashboard dashboard;
    public static CommitHistoryDashboardModule commitHistoryModule;
    public static EventDashboardModule eventModule;
    public static LeaderboardDashboardModule leaderboardModule;
    public static LineDiagramDashboardModule lineDiagramModule;
    public static QueueDashboardModule queueModule;
    public static Dashboard noNameDashboard;

    @BeforeEach
    void initDashboardAndModules() {
        dashboard = new Dashboard("test");
        noNameDashboard = new Dashboard();

        commitHistoryModule = new CommitHistoryDashboardModule(1);
        commitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository"));

        eventModule = new EventDashboardModule(2);

        leaderboardModule = new LeaderboardDashboardModule(3);
        leaderboardModule.setBenchmarkOfLeaderboard("testBenchmark");

        lineDiagramModule = new LineDiagramDashboardModule(4);
        lineDiagramModule.setTrackedBenchmarks(Arrays.asList("testBenchmark"));
        lineDiagramModule.setTrackedRepositories(Arrays.asList("testRepository"));

        queueModule = new QueueDashboardModule(5);
    }

    @Test
    void Constructor_InvalidTitle_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Dashboard(null));
        assertThrows(IllegalArgumentException.class, () -> new Dashboard(""));
        assertThrows(IllegalArgumentException.class, () -> new Dashboard(" "));
    }

    @Test
    void addModule_ValidPositionsOnDashboard_ShouldNotThrowException() {
        DashboardModuleDummy dashboardModule1 = new DashboardModuleDummy(0);
        DashboardModuleDummy dashboardModule2 = new DashboardModuleDummy(4);
        DashboardModuleDummy dashboardModule3 = new DashboardModuleDummy(5);
        DashboardModuleDummy dashboardModule4 = new DashboardModuleDummy(14);

        dashboardModule.setPosition(6);
        dashboard.addModule(dashboardModule);
        assertFalse(dashboard.removeModule(7));
    }

    @Test
    void removeModule_NonEmptyPositionGiven_ShouldReturnTrue() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy(3);

        dashboard.addModule(dashboardModule);
        assertTrue(dashboard.removeModule(3));
    }

    @Test
    void removeModule_PositionTooSmallGiven_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy(0);

        dashboard.addModule(dashboardModule);
        assertThrows(IllegalArgumentException.class, () -> dashboard.removeModule(-1));
    }

    @Test
    void removeModule_PositionTooBigGiven_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy(14);

        dashboard.addModule(dashboardModule);
        assertThrows(IllegalArgumentException.class, () -> dashboard.removeModule(15));
    }

    @Test
    void removeModule_NonExistingDashboardModuleGiven_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModule1 = new DashboardModuleDummy(0);
        DashboardModuleDummy dashboardModule2 = new DashboardModuleDummy(14);

        dashboard.addModule(dashboardModule1);
        assertFalse(dashboard.removeModule(dashboardModule2));
    }

    @Test
    void removeModule_ExistingDashboardModuleGiven_ShouldReturnTrue() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy(0);

        dashboard.addModule(dashboardModule);
        assertTrue(dashboard.removeModule(dashboardModule));
    }

    @Test
    void removeModule_NullDashboardModuleGiven_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModule = null;

        assertFalse(dashboard.removeModule(dashboardModule));
    }

    @Test
    void updateLastAccess_ShouldUpdateLastAccess() {
        LocalDate now = LocalDate.now();
        dashboard.updateLastAccess();
        assertEquals(now, dashboard.getLastAccess());
    }

    @Test
    void getLeaderboardModules_NoModules_SouldReturnEmptyCollection() {
        assertTrue(dashboard.getLeaderboardModules().isEmpty());
    }

    @Test
    void getLeaderboardModules_NoLeaderboardModules_ShouldReturnEmptyCollection() {
        dashboard.addModule(commitHistoryModule);
        dashboard.addModule(eventModule);
        dashboard.addModule(lineDiagramModule);
        dashboard.addModule(queueModule);

        assertTrue(dashboard.getLeaderboardModules().isEmpty());
    }

    @Test
    void getLeaderboardModules_OnlyContainsLeaderboardModule_ShouldReturnCollectionWithLeaderboardModule() {
        dashboard.addModule(leaderboardModule);

        assertTrue(dashboard.getLeaderboardModules().contains(leaderboardModule));
    }

    @Test
    void getLeaderboardModules_ContainsLeaderboardModule_ShouldReturnCollectionWithLeaderboardModule() {
        dashboard.addModule(commitHistoryModule);
        dashboard.addModule(eventModule);
        dashboard.addModule(lineDiagramModule);
        dashboard.addModule(queueModule);
        dashboard.addModule(leaderboardModule);

        assertTrue(dashboard.getLeaderboardModules().contains(leaderboardModule));
    }

    @Test
    void getLeaderboardModules_ContainsManyLeaderboardModules_ShouldReturnCollectionWithLeaderboardModules() {
        ArrayList<LeaderboardDashboardModule> ldmList = new ArrayList<LeaderboardDashboardModule>();

        for (int i = 0; i < 15; i++) {
            LeaderboardDashboardModule ldm = new LeaderboardDashboardModule(i);
            ldm.setBenchmarkOfLeaderboard("testBenchmark");
            dashboard.addModule(ldm);
            ldmList.add(ldm);
        }

        assertTrue(dashboard.getLeaderboardModules().containsAll(ldmList));
    }

    @Test
    void setEditKey_normalKey_KeyWasChanged() {
        final String FIRST_EDIT_KEY = "first edit key";
        final String SECOND_EDIT_KEY = "new edit key";

        dashboard.setEditKey(FIRST_EDIT_KEY);
        dashboard.setEditKey(SECOND_EDIT_KEY);

        String actualKey = dashboard.getEditKey();

        assertNotEquals(FIRST_EDIT_KEY, actualKey);
        assertEquals(SECOND_EDIT_KEY, actualKey);
    }

    @Test
    void setViewKey_normalKey_KeyWasChanged() {
        final String FIRST_VIEW_KEY = "The first view key";
        final String SECOND_VIEW_KEY = "Now, the second view key";

        dashboard.setViewKey(FIRST_VIEW_KEY);
        dashboard.setViewKey(SECOND_VIEW_KEY);

        String actualKey = dashboard.getViewKey();

        assertNotEquals(FIRST_VIEW_KEY, actualKey);
        assertEquals(SECOND_VIEW_KEY, actualKey);
    }

    @Test
    void getId_ShouldReturnId() {
        assertDoesNotThrow(() -> dashboard.getId());
    }


}
