package pacr.webapp_backend.dashboard_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.LeaderboardDashboardModule;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardManagerTest extends SpringBootTestWithoutShell {

    private final DashboardManager dashboardManager;

    private Dashboard dashboard;

    private Dashboard dashboardClone;

    private  String editKey;
    private  String viewKey;

    @Autowired
    DashboardManagerTest (final DashboardManager dashboardManager) {
        this.dashboardManager = dashboardManager;
    }

    @BeforeEach
    void init() {
        dashboard = new Dashboard("test");

        final Pair<String, String> keys = dashboardManager.addDashboard(dashboard);

        viewKey = keys.getFirst();
        editKey = keys.getSecond();

        dashboardClone = new Dashboard();
        dashboardClone.initializeKeys(editKey, viewKey);
    }

    @AfterEach
    void cleanUp() {
        dashboardManager.databaseTalker.deletionIntervalAccess.delete();

        for (final Dashboard d : dashboardManager.databaseTalker.getAllDashboards()) {
            try {
                dashboardManager.deleteDashboard(d.getEditKey());
            } catch (final IllegalAccessException e) {
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
        assertEquals(dashboard, dashboardManager.getDashboard(editKey));
        assertEquals(dashboard.getId(), dashboardManager.getDashboard(viewKey).getId());
    }

    @Test
    void addDashboard_NullDashboard_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> dashboardManager.addDashboard(null));
    }

    @Test
    void addDashboard_UninitializedDashboard_ShouldReturnKeys() {
        final Dashboard dashboard = new Dashboard();

        final Pair<String,String> keys = dashboardManager.addDashboard(dashboard);

        assertNotEquals(null, keys.getFirst());
        assertNotEquals(null, keys.getSecond());

        assertDoesNotThrow(() -> dashboardManager.getDashboard(keys.getFirst()));
    }

    @Test
    void addDashboard_PreInitializedDashboard_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> dashboardManager.addDashboard(dashboardClone));
    }

    @Test
    void updateDashboard_NullDashboard_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> dashboardManager.updateDashboard(null));
    }

    @Test
    void updateDashboard_AlreadyExistingDashboard_ShouldChangeDashboard() {
        dashboardClone.addModule(new LeaderboardDashboardModule());

        assertDoesNotThrow(() -> dashboardManager.updateDashboard(dashboardClone));

        assertEquals(dashboardClone, dashboardManager.getDashboard(editKey));
        assertNotEquals(dashboard, dashboardManager.getDashboard(editKey));
    }

    @Test
    void updateDashboard_ViewKeyInDashboard_ShouldThrowException() {
        final Dashboard dashboardClone = new Dashboard("title");
        dashboardClone.initializeKeys(viewKey, editKey);

        assertThrows(IllegalAccessException.class, () -> dashboardManager.updateDashboard(dashboardClone));
    }

    @Test
    void updateDashboard_InvalidKeyInDashboard_ShouldThrowException() {
        final Dashboard dashboardClone = new Dashboard("title");
        dashboardClone.initializeKeys("404", viewKey);

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
