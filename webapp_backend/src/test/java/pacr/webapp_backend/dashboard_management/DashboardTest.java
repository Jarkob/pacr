package pacr.webapp_backend.dashboard_management;

import static org.junit.jupiter.api.Assertions.*;

import net.bytebuddy.pool.TypePool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class DashboardTest {


    public Dashboard dashboard;
    public CommitHistoryDashboardModule commitHistoryModule;
    public EventDashboardModule eventModule;
    public LeaderboardDashboardModule leaderboardModule;
    public LineDiagramDashboardModule lineDiagramModule;
    public QueueDashboardModule queueModule;
    public Dashboard noNameDashboard;

    @BeforeEach
    void initDashboard() {
        dashboard = new Dashboard("test");
        noNameDashboard = new Dashboard();

        commitHistoryModule = new CommitHistoryDashboardModule(1);
        commitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository"));

        eventModule = new EventDashboardModule(2);

        leaderboardModule = new LeaderboardDashboardModule(3);
        leaderboardModule.setBenchmarkName("testBenchmark");

        lineDiagramModule = new LineDiagramDashboardModule(4);
        lineDiagramModule.setTrackedBenchmarks(Arrays.asList("testBenchmark"));
        lineDiagramModule.setTrackedRepositories(Arrays.asList("testRepository"));

        queueModule = new QueueDashboardModule(5);
    }

    @Test
    void addModule_ValidPositionsOnDashboard_ShouldNotThrowException() {
        DashboardModuleDummy dashboardModule1 = new DashboardModuleDummy(0);
        DashboardModuleDummy dashboardModule2 = new DashboardModuleDummy(4);
        DashboardModuleDummy dashboardModule3 = new DashboardModuleDummy(5);
        DashboardModuleDummy dashboardModule4 = new DashboardModuleDummy(14);

        assertDoesNotThrow(() -> dashboard.addModule(dashboardModule1));
        assertDoesNotThrow(() -> dashboard.addModule(dashboardModule2));
        assertDoesNotThrow(() -> dashboard.addModule(dashboardModule3));
        assertDoesNotThrow(() -> dashboard.addModule(dashboardModule4));
    }


    @Test
    void addModule_OverloadPositionOnDashboard_ShouldThrowException() {
        DashboardModuleDummy dashboardModule1 = new DashboardModuleDummy(8);
        DashboardModuleDummy dashboardModule2 = new DashboardModuleDummy(8);

        dashboard.addModule(dashboardModule1);
        assertThrows(IllegalArgumentException.class, () -> dashboard.addModule(dashboardModule2));
    }

    @Test
    void addModule_UninitializedDashboardModule_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertThrows(IllegalArgumentException.class, () -> dashboard.addModule(dashboardModule));
    }

    @Test
    void addModule_NullDashboardModule_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = null;

        assertThrows(NullPointerException.class, () -> dashboard.addModule(dashboardModule));
    }

    @Test
    void removeModule_EmptyPositionGiven_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy(6);

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
            ldm.setBenchmarkName("testBenchmark");
            dashboard.addModule(ldm);
            ldmList.add(ldm);
        }

        assertTrue(dashboard.getLeaderboardModules().containsAll(ldmList));
    }

    @Test
    void initializeKeys_UsualValues_KeysGetChanged() {
        final String EDIT_KEY = "the edit key";
        final String VIEW_KEY = "the view key";

        dashboard.initializeKeys(EDIT_KEY, VIEW_KEY);

        String actualEditKey = dashboard.getEditKey();
        String actualViewKey = dashboard.getViewKey();

        assertEquals(EDIT_KEY, actualEditKey);
        assertEquals(VIEW_KEY, actualViewKey);
    }

    @Test
    void initializeKeys_SecondAccess_ShouldThrowException() {
        final String EDIT_KEY = "edit key";
        final String VIEW_KEY = "view key";

        dashboard.initializeKeys(EDIT_KEY, VIEW_KEY);
        assertThrows(KeysAlreadyInitializedException.class, () -> dashboard.initializeKeys(EDIT_KEY, VIEW_KEY));
    }

    @Test
    void initializeKeys_NullValues_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> dashboard.initializeKeys(null, null));
    }

    @Test
    void prepareForViewAccess_ShouldSetEditKeyNull() {
        final String EDIT_KEY = "an edit key";
        final String VIEW_KEY = "a view key";
        dashboard.initializeKeys(EDIT_KEY, VIEW_KEY);

        dashboard.prepareForViewAccess();

        assertNull(dashboard.getEditKey());
    }

    @Test
    void getId_ShouldReturnId() {
        assertDoesNotThrow(() -> dashboard.getId());
    }

    @Test
    void getTitle_ShouldReturnTitle() {
        assertEquals("test", dashboard.getTitle());
    }

}
