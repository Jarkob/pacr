package pacr.webapp_backend.dashboard_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.LeaderboardDashboardModule;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DashboardManagerTest {

    private DashboardManager dashboardManager;

    private Dashboard dashboard;

    private Dashboard dashboardClone;

    private static final String EDIT_KEY = "edit key";
    private static final String VIEW_KEY = "view key";

    @Autowired
    DashboardManagerTest (DashboardManager dashboardManager) {
        this.dashboardManager = dashboardManager;
    }

    @BeforeEach
    void init() {
        dashboard = new Dashboard("test");

        dashboard.initializeKeys(EDIT_KEY, VIEW_KEY);

        dashboardManager.addDashboard(dashboard);


        dashboardClone = new Dashboard();
        dashboardClone.initializeKeys(EDIT_KEY, VIEW_KEY);
    }

    @AfterEach
    void cleanUp() {
        dashboardManager.databaseTalker.deletionIntervalAccess.delete();

        for (Dashboard d : dashboardManager.databaseTalker.getAllDashboards()) {
            try {
                dashboardManager.deleteDashboard(d.getEditKey());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void getDashboard_NullKey_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> dashboardManager.getDashboard(null));
    }

    @Test
    void getDashboard_NoFittingDashboard_ShouldThrowException() {
        assertThrows(NoSuchElementException.class, () ->dashboardManager.getDashboard("404"));
    }

    @Test
    void getDashboard_ExistingKeys_ShouldReturnDashboard() {
        assertEquals(dashboard, dashboardManager.getDashboard(EDIT_KEY));
        assertEquals(dashboard.getId(), dashboardManager.getDashboard(VIEW_KEY).getId());
    }

    @Test
    void addDashboard_NullDashboard_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> dashboardManager.addDashboard(null));
    }

    @Test
    void addDashboard_UninitializedDashboard_ShouldReturnKeys() {
        Dashboard dashboard = new Dashboard();

        Pair<String,String> keys = dashboardManager.addDashboard(dashboard);

        assertNotEquals(null, keys.getFirst());
        assertNotEquals(null, keys.getSecond());

        assertDoesNotThrow(() -> dashboardManager.getDashboard(keys.getFirst()));
    }

    @Test
    void addDashboard_InitializedDashboard_ShouldNotChangeKeys() {
        Pair<String,String> keys = dashboardManager.addDashboard(dashboard);

        assertEquals(VIEW_KEY, keys.getFirst());
        assertEquals(EDIT_KEY, keys.getSecond());

        assertDoesNotThrow(() -> dashboardManager.getDashboard(EDIT_KEY));
    }

    @Test
    void updateDashboard_NullDashboard_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> dashboardManager.updateDashboard(null));
    }

    @Test
    void updateDashboard_AlreadyExistingDashboard_ShouldChangeDashboard() {
        dashboardClone.addModule(new LeaderboardDashboardModule());

        assertDoesNotThrow(() -> dashboardManager.updateDashboard(dashboardClone));

        assertEquals(dashboardClone, dashboardManager.getDashboard(EDIT_KEY));
        assertNotEquals(dashboard, dashboardManager.getDashboard(EDIT_KEY));
    }

    @Test
    void updateDashboard_ViewKeyInDashboard_ShouldThrowException() {
        Dashboard dashboardClone = new Dashboard("title");
        dashboardClone.initializeKeys(VIEW_KEY, EDIT_KEY);

        assertThrows(IllegalAccessException.class, () -> dashboardManager.updateDashboard(dashboardClone));
    }

    @Test
    void updateDashboard_InvalidKeyInDashboard_ShouldThrowException() {
        Dashboard dashboardClone = new Dashboard("title");
        dashboardClone.initializeKeys("404", VIEW_KEY);

        assertThrows(NoSuchElementException.class, () -> dashboardManager.updateDashboard(dashboardClone));
    }

    @Test
    void setDeletionInterval_ValueLessEqualZero_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> dashboardManager.setDeletionInterval(0));
        assertThrows(IllegalArgumentException.class, () -> dashboardManager.setDeletionInterval(-1));
    }

    @Test
    void setDeletionInterval_ValueAboveZero_ShouldChangeDeletionInterval() {
        assertDoesNotThrow(() -> dashboardManager.setDeletionInterval(1));
        assertEquals(1, dashboardManager.getDeletionInterval());
    }

    @Test
    void getDeletionInterval_NotInitialized_ShouldReturnDefault() {
        assertEquals(10, dashboardManager.getDeletionInterval());
    }

    @Test
    void deleteOldDashboards_NoDeletions() {
        assertDoesNotThrow(() -> dashboardManager.deleteOldDashboards());
    }


}
