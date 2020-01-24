package pacr.webapp_backend.dashboard_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DashboardModuleTest {

    @Test
    void equals_differentClass_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertNotEquals(dashboardModule, new Object());
    }

    @Test
    void equals_sameClass_ShouldReturnTrue() {
        DashboardModuleDummy dashboardModuleA = new DashboardModuleDummy();
        DashboardModuleDummy dashboardModuleB = new DashboardModuleDummy();

        assertEquals(dashboardModuleA, dashboardModuleB);
    }


}
