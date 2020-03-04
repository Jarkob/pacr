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
    void constructor_emptyTitle_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Dashboard(""));
        assertThrows(IllegalArgumentException.class, () -> new Dashboard(null));
        assertThrows(IllegalArgumentException.class, () -> new Dashboard(" "));
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
        final DashboardModuleDummy dashboardModule = null;

        assertThrows(NullPointerException.class, () -> dashboard.addModule(dashboardModule));
        assertThrows(NullPointerException.class, () -> dashboard.addModule(dashboardModule, 4));
    }

    @Test
    void removeModule_EmptyPositionGiven_ShouldReturnFalse() {
        final DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

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
        final DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboard.addModule(dashboardModule);
        assertThrows(IndexOutOfBoundsException.class, () -> dashboard.removeModule(-1));
    }

    @Test
    void removeModule_PositionTooBigGiven_ShouldThrowException() {
        final DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboard.addModule(dashboardModule);
        assertThrows(IndexOutOfBoundsException.class, () -> dashboard.removeModule(15));
    }

    @Test
    void removeModule_ExistingDashboardModuleGiven_ShouldReturnTrue() {
        final DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboard.addModule(dashboardModule);
        assertTrue(dashboard.removeModule(dashboardModule));
    }

    @Test
    void removeModule_NullDashboardModuleGiven_ShouldReturnFalse() {
        final DashboardModuleDummy dashboardModule = null;

        assertThrows(NullPointerException.class, () -> dashboard.removeModule(dashboardModule));
    }

    @Test
    void updateLastAccess_ShouldUpdateLastAccess() {
        final LocalDate now = LocalDate.now();
        dashboard.updateLastAccess();
        assertEquals(now, dashboard.getLastAccess());
    }

    @Test
    void initializeKeys_UsualValues_KeysGetChanged() {
        final String EDIT_KEY = "the edit key";
        final String VIEW_KEY = "the view key";

        dashboard.initializeKeys(EDIT_KEY, VIEW_KEY);

        final String actualEditKey = dashboard.getEditKey();
        final String actualViewKey = dashboard.getViewKey();

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

    @Test
    void equals_SameObject_ShouldReturnTrue() {
        assertEquals(dashboard, dashboard);
    }

    @Test
    void equals_DifferentSimpleObjectSameAttributes_ShouldReturnTrue() {
        final Dashboard dashboard1 = new Dashboard();
        final Dashboard dashboard2 = new Dashboard();

        assertEquals(dashboard1, dashboard2);
    }

    @Test
    void equals_NullObject_ShouldReturnFalse() {
        assertNotEquals(dashboard, null);
    }

    @Test
    void equals_OtherClass_ShouldReturnFalse() {
        assertNotEquals(dashboard, new DashboardModuleDummy());
    }

    @Test
    void equals_DifferentTitles_ShouldReturnFalse() {

        final Dashboard dashboard1 = new Dashboard("title_a");
        final Dashboard dashboard2 = new Dashboard("title_b");

        assertNotEquals(dashboard1, dashboard2);
    }

    @Test
    void equals_DifferentViewKey_ShouldReturnFalse() {

        final Dashboard dashboard1 = new Dashboard("title");
        final Dashboard dashboard2 = new Dashboard("title");

        dashboard1.initializeKeys("Different", "Same");
        dashboard2.initializeKeys("Not The Same", "Same");

        assertNotEquals(dashboard1, dashboard2);
    }

    @Test
    void equals_DifferentEditKey_ShouldReturnFalse() {

        final Dashboard dashboard1 = new Dashboard("title");
        final Dashboard dashboard2 = new Dashboard("title");

        dashboard1.initializeKeys("Same", "Different");
        dashboard2.initializeKeys("Same", "Not The Same");

        assertNotEquals(dashboard1, dashboard2);
    }

    @Test
    void equals_DifferentAmountOfModules_ShouldReturnFalse() {

        final Dashboard dashboard1 = new Dashboard("title");
        final Dashboard dashboard2 = new Dashboard("title");

        dashboard1.addModule(commitHistoryModule);


        assertNotEquals(dashboard1, dashboard2);
    }

    @Test
    void equals_DifferentModules_ShouldReturnFalse() {
        final Dashboard dashboard1 = new Dashboard("title");
        final Dashboard dashboard2 = new Dashboard("title");

        dashboard1.addModule(leaderboardModule);
        dashboard2.addModule(lineDiagramModule);

        assertNotEquals(dashboard1, dashboard2);

    }

    @Test
    void equals_DifferentComplexObjectSameAttributes_ShouldReturnTrue() {
        final Dashboard dashboard1 = new Dashboard("test_title");
        final Dashboard dashboard2 = new Dashboard("test_title");

        dashboard1.initializeKeys("test_edit_key", "test_view_key");
        dashboard2.initializeKeys("test_edit_key", "test_view_key");

        dashboard1.updateLastAccess();
        dashboard2.updateLastAccess();



        dashboard1.addModule(commitHistoryModule);

        commitHistoryModule = new CommitHistoryDashboardModule();
        commitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository"));
        dashboard2.addModule(commitHistoryModule);


        dashboard1.addModule(eventModule);

        eventModule = new EventDashboardModule();
        dashboard2.addModule(eventModule);


        dashboard1.addModule(leaderboardModule);

        leaderboardModule = new LeaderboardDashboardModule();
        leaderboardModule.setLeaderboard(new Leaderboard());
        dashboard2.addModule(leaderboardModule);


        dashboard1.addModule(lineDiagramModule);

        lineDiagramModule = new LineDiagramDashboardModule();
        lineDiagramModule.setTrackedBenchmarks(Arrays.asList("testBenchmark"));
        lineDiagramModule.setTrackedRepositories(Arrays.asList("testRepository"));
        dashboard2.addModule(lineDiagramModule);


        dashboard1.addModule(queueModule);

        queueModule = new QueueDashboardModule();
        dashboard2.addModule(queueModule);

        assertTrue(dashboard1.equals(dashboard2));
    }

}
