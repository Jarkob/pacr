package pacr.webapp_backend.dashboard_management.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DashboardTest {


    public static Dashboard dashboard;

    @BeforeEach
    void initDashboard() {
        dashboard = new Dashboard("test");
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

        assertThrows(IllegalArgumentException.class, () -> dashboard.addModule(dashboardModule));
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
}
