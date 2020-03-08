package pacr.webapp_backend.dashboard_management.endpoints;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Commit;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.dashboard_management.CommitHistoryDashboardModule;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.LineDiagramDashboardModule;
import pacr.webapp_backend.dashboard_management.services.DashboardManager;
import pacr.webapp_backend.database.DashboardDB;

import javax.activation.DataHandler;

import static org.junit.jupiter.api.Assertions.*;

public class ManageDashboardControllerTest extends SpringBootTestWithoutShell {

    ManageDashboardController manageDashboardController;

    DashboardDB dashboardDB;

    private Dashboard dashboard;

    private String viewKey;

    private String editKey;

    private static final String KEY = "key";

    private final DashboardManager dashboardManager;

    @Autowired
    ManageDashboardControllerTest(final DashboardManager dashboardManager, final DashboardDB dashboardDB) {
        this.dashboardManager = dashboardManager;
        this.dashboardDB = dashboardDB;
    }

    @BeforeEach
    void init() {
        this.manageDashboardController = new ManageDashboardController(dashboardManager);

        dashboard = new Dashboard("test");

        final Pair<String, String> keys =
                ((Pair<String, String>) manageDashboardController.addDashboard(dashboard).getBody());

        assertNotNull(keys);
        viewKey = keys.getFirst();
        editKey = keys.getSecond();
    }

    @AfterEach
    void cleanUp() {
        dashboardDB.deleteAll();
    }

    @Test
    void addDashboard_UsualDashboard_ShouldBeAdded() {
        assertEquals(dashboard, manageDashboardController.getDashboard(editKey).getBody());
    }

    @Test
    void addDashboard_ExistingDashboard_ShouldReturnBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST, manageDashboardController.addDashboard(dashboard).getStatusCode());
    }

    @Test
    void getDashboard_DashboardExistsWithKey_ShouldReturnDashboard() {

        assertEquals(dashboard, manageDashboardController.getDashboard(editKey).getBody());

        assertNotEquals(dashboard, manageDashboardController.getDashboard(viewKey).getBody());
    }

    @Test
    void getDashboard_DashboardDoesNotExist_ShouldReturnNotFound() {

        assertEquals(HttpStatus.NOT_FOUND, manageDashboardController.getDashboard(KEY).getStatusCode());
    }

    @Test
    void updateDashboard_NormalDashboard_ShouldUpdateDashboard() {
        final Dashboard retrievedDashboard = ((Dashboard) manageDashboardController.getDashboard(editKey).getBody());
        retrievedDashboard.addModule(new CommitHistoryDashboardModule());

        manageDashboardController.updateDashboard(retrievedDashboard);

        retrievedDashboard.removeModule(0);

        assertNotEquals(retrievedDashboard, manageDashboardController.getDashboard(editKey).getBody());

    }

    @Test
    void updateDashboard_NoSuchDashboard_ShouldReturnNotFound() {
        final Dashboard dashboardClone = new Dashboard("test");

        dashboardClone.initializeKeys(KEY, KEY);

        assertEquals(HttpStatus.NOT_FOUND, manageDashboardController.updateDashboard(dashboardClone).getStatusCode());
    }

    @Test
    void updateDashboard_InvalidKey_ShouldReturnUnauthorized() {
        final Dashboard dashboardClone = new Dashboard("test");

        dashboardClone.initializeKeys(viewKey, viewKey);

        assertEquals(HttpStatus.UNAUTHORIZED, manageDashboardController.updateDashboard(dashboardClone).getStatusCode());
    }

    @Test
    void deleteDashboard_ExistingDashboard_ShouldDeleteDashboard() {
        manageDashboardController.deleteDashboard(editKey);

        assertEquals(HttpStatus.NOT_FOUND, manageDashboardController.getDashboard(KEY).getStatusCode());
    }

    @Test
    void deleteDashboard_KeyTooWeak_ShouldReturnUnauthorized() {
        assertEquals(HttpStatus.UNAUTHORIZED, manageDashboardController.deleteDashboard(viewKey).getStatusCode());
    }

    @Test
    void deleteDashboard_KeyDoesNotExist_ShouldReturnNotFound() {
        assertEquals(HttpStatus.NOT_FOUND, manageDashboardController.deleteDashboard(KEY).getStatusCode());
    }
}
