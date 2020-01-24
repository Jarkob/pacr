package pacr.webapp_backend.dashboard_management;

import static org.junit.jupiter.api.Assertions.*;

import net.bytebuddy.pool.TypePool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.leaderboard_management.Leaderboard;

import javax.swing.text.AbstractDocument;
import java.time.LocalDate;
import java.util.Arrays;

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

        commitHistoryModule = new CommitHistoryDashboardModule();
        commitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository"));

        eventModule = new EventDashboardModule();

        leaderboardModule = new LeaderboardDashboardModule();
        leaderboardModule.setLeaderboard(new Leaderboard());

        lineDiagramModule = new LineDiagramDashboardModule();
        lineDiagramModule.setTrackedBenchmarks(Arrays.asList("testBenchmark"));
        lineDiagramModule.setTrackedRepositories(Arrays.asList("testRepository"));

        queueModule = new QueueDashboardModule();
    }

    @Test
    void addModule_ValidPositionsOnDashboard_ShouldNotThrowException() {

        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));
        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));
        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));
        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));

        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy(), 0));
        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy(), 5));
    }

    @Test
    void addModule_InvalidPositionsOnDashboard_ShouldThrowException() {

        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));
        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));
        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));
        assertDoesNotThrow(() -> dashboard.addModule(new DashboardModuleDummy()));

        assertThrows(IndexOutOfBoundsException.class,
                () -> dashboard.addModule(new DashboardModuleDummy(), -1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> dashboard.addModule(new DashboardModuleDummy(), 6));
    }

    @Test
    void addModule_FullDashboard_ShouldThrowException() {
        for (int i = 0; i < Dashboard.SIZE; i++) {
            dashboard.addModule(new DashboardModuleDummy());
        }

        assertThrows(DashboardFullException.class, () -> dashboard.addModule(new DashboardModuleDummy()));
        assertThrows(DashboardFullException.class, () -> dashboard.addModule(new DashboardModuleDummy(), 7));
    }

    @Test
    void addModule_NullDashboardModule_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = null;

        assertThrows(NullPointerException.class, () -> dashboard.addModule(dashboardModule));
        assertThrows(NullPointerException.class, () -> dashboard.addModule(dashboardModule, 4));
    }

    @Test
    void removeModule_EmptyPositionGiven_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboard.addModule(dashboardModule);
        assertFalse(dashboard.removeModule(7));
    }

    @Test
    void removeModule_NonEmptyPositionGiven_ShouldReturnTrue() {
        dashboard.addModule(new DashboardModuleDummy());
        dashboard.addModule(new DashboardModuleDummy());
        dashboard.addModule(new DashboardModuleDummy());

        assertTrue(dashboard.removeModule(2));
    }

    @Test
    void removeModule_PositionTooSmallGiven_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboard.addModule(dashboardModule);
        assertThrows(IndexOutOfBoundsException.class, () -> dashboard.removeModule(-1));
    }

    @Test
    void removeModule_PositionTooBigGiven_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboard.addModule(dashboardModule);
        assertThrows(IndexOutOfBoundsException.class, () -> dashboard.removeModule(15));
    }

    @Test
    void removeModule_ExistingDashboardModuleGiven_ShouldReturnTrue() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboard.addModule(dashboardModule);
        assertTrue(dashboard.removeModule(dashboardModule));
    }

    @Test
    void removeModule_NullDashboardModuleGiven_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModule = null;

        assertThrows(NullPointerException.class, () -> dashboard.removeModule(dashboardModule));
    }

    @Test
    void updateLastAccess_ShouldUpdateLastAccess() {
        LocalDate now = LocalDate.now();
        dashboard.updateLastAccess();
        assertEquals(now, dashboard.getLastAccess());
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
