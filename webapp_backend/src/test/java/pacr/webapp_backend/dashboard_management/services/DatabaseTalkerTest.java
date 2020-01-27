package pacr.webapp_backend.dashboard_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.LineDiagramDashboardModule;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseTalkerTest {

    private DatabaseTalker databaseTalker;

    private Dashboard dashboard;

    private Dashboard dashboardClone;



    private static final String EDIT_KEY = "edit key";
    private static final String VIEW_KEY = "view key";

    @Autowired
    DatabaseTalkerTest (DatabaseTalker databaseTalker) {
        this.databaseTalker = databaseTalker;
    }

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);

        dashboard = new Dashboard();
        dashboard.initializeKeys(EDIT_KEY, VIEW_KEY);

        dashboardClone = new Dashboard();
        dashboardClone.initializeKeys(EDIT_KEY, VIEW_KEY);

    }

    @AfterEach
    void cleanUpDatabase() {
        databaseTalker.deletionIntervalAccess.delete();

        for (Dashboard d : databaseTalker.getAllDashboards()) {
            try {
                databaseTalker.deleteDashboard(d.getEditKey());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void storeDashboard_NormalDashboard_ShouldStoreDashboard() {
        databaseTalker.storeDashboard(dashboard);

        assertEquals(dashboard, databaseTalker.getDashboard(EDIT_KEY));
    }

    @Test
    void updateDashboard_UsualDashboard_ShouldUpdateDashboard() {
        databaseTalker.storeDashboard(dashboard);

        dashboardClone.addModule(new LineDiagramDashboardModule());

        assertDoesNotThrow(() -> databaseTalker.updateDashboard(dashboardClone));
        assertNotEquals(dashboard, databaseTalker.getDashboard(EDIT_KEY));
        assertEquals(dashboardClone, databaseTalker.getDashboard(EDIT_KEY));
    }

    @Test
    void updateDashboard_NoEditKey_ShouldThrowException() {
        databaseTalker.storeDashboard(dashboard);

        dashboardClone.prepareForViewAccess();


        assertThrows(IllegalAccessException.class, () -> databaseTalker.updateDashboard(dashboardClone));
    }

    @Test
    void updateDashboard_ViewKeyInsteadEditKey_ShouldThrowException() {
        databaseTalker.storeDashboard(dashboard);

        dashboardClone = new Dashboard();
        dashboardClone.initializeKeys(VIEW_KEY, EDIT_KEY);

        assertThrows(IllegalAccessException.class, () -> databaseTalker.updateDashboard(dashboardClone));
    }

    @Test
    void updateDashboard_InvalidEditKey_ShouldThrowException() {
        databaseTalker.storeDashboard(dashboard);

        dashboardClone = new Dashboard();
        dashboardClone.initializeKeys("404", VIEW_KEY);

        assertThrows(NoSuchElementException.class, () -> databaseTalker.updateDashboard(dashboardClone));
    }

    @Test
    void deleteDashboard_ExistingKey_ShouldDeleteDashboard() {
        databaseTalker.storeDashboard(dashboard);

        assertDoesNotThrow(() -> databaseTalker.getDashboard(EDIT_KEY));

        assertDoesNotThrow(() -> databaseTalker.deleteDashboard(EDIT_KEY));

        assertThrows(NoSuchElementException.class, () -> databaseTalker.getDashboard(EDIT_KEY));
    }

    @Test
    void deleteDashboard_TryDeletionWithViewKey_ShouldThrowException() {
        databaseTalker.storeDashboard(dashboard);

        assertDoesNotThrow(() -> databaseTalker.getDashboard(EDIT_KEY));

        assertThrows(IllegalAccessException.class, () -> databaseTalker.deleteDashboard(VIEW_KEY));
    }

    @Test
    void deleteDashboard_TryDeletionWithNonExistingKey_ShouldThrowException() {
        databaseTalker.storeDashboard(dashboard);

        assertDoesNotThrow(() -> databaseTalker.getDashboard(EDIT_KEY));

        assertThrows(NoSuchElementException.class, () -> databaseTalker.deleteDashboard("404"));
    }

    @Test
    void getDeletionInterval_DeletionIntervalNotSet_ShouldReturnDefaultInterval() {
        assertEquals(DatabaseTalker.DEFAULT_DELETION_INTERVAL, databaseTalker.getDeletionInterval());
    }

    @Test
    void getDeletionInterval_DeletionIntervalSet_ShouldReturnInterval() {
        databaseTalker.setDeletionInterval(40);

        assertEquals(40, databaseTalker.getDeletionInterval());

        databaseTalker.setDeletionInterval(1);

        assertEquals(1, databaseTalker.getDeletionInterval());
    }

    @Test
    void getDashboard_NotExistingKey_ShouldThrowException() {
        databaseTalker.storeDashboard(dashboard);

        assertThrows(NoSuchElementException.class, () -> databaseTalker.getDashboard("404"));
    }

    @Test
    void getDashboard_ExistingEditKey_ShouldReturnDashboard() {
        databaseTalker.storeDashboard(dashboard);

        assertEquals(dashboard, databaseTalker.getDashboard(EDIT_KEY));
    }

    @Test
    void getDashboardExistingViewKey_ShouldReturnSimilarDashboard() {
        databaseTalker.storeDashboard(dashboard);

        assertNotEquals(dashboard, databaseTalker.getDashboard(VIEW_KEY));
    }


}
