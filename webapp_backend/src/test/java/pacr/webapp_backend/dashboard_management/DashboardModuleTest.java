package pacr.webapp_backend.dashboard_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DashboardModuleTest {



    @Test
    void setPosition_ValidExtremePositions_ShouldNotThrowException_ShouldSetCorrectValues() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertDoesNotThrow(() -> dashboardModule.setPosition(DashboardModule.SIZE - 1));
        assertEquals(dashboardModule.getPosition(), DashboardModule.SIZE - 1);

        assertDoesNotThrow(() -> dashboardModule.setPosition(0));
        assertEquals(dashboardModule.getPosition(), 0);
    }


    @Test
    void setPosition_TooLowPosition_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertThrows(IllegalArgumentException.class,
                () -> dashboardModule.setPosition(-1));
    }

    @Test
    void setPosition_TooHighPosition_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertThrows(IllegalArgumentException.class,
                () -> dashboardModule.setPosition(DashboardModule.SIZE));
    }

    @Test
    void getPosition_ValidPositions_ShouldSetCorrectValues() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboardModule.setPosition(7);
        assertEquals(7, dashboardModule.getPosition());

        dashboardModule.setPosition(DashboardModule.SIZE - 1);
        assertEquals(dashboardModule.getPosition(), DashboardModule.SIZE - 1);

        dashboardModule.setPosition(0);
        assertEquals(dashboardModule.getPosition(), 0);
    }

    @Test
    void getPosition_ValidPositionFromConstructor_ShouldSetCorrectValues() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy(3);
        assertEquals(3, dashboardModule.getPosition());

        dashboardModule = new DashboardModuleDummy(0);
        assertEquals(0, dashboardModule.getPosition());

        dashboardModule = new DashboardModuleDummy(DashboardModule.SIZE - 1);
        assertEquals(DashboardModule.SIZE - 1, dashboardModule.getPosition());
    }

    @Test
    void getPosition_PositionNotInitialized_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();
        assertThrows(IllegalStateException.class,
                dashboardModule::getPosition);
    }

    @Test
    void equals_differentClass_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertNotEquals(dashboardModule, new Object());
    }

    @Test
    void equals_sameClassDifferentPosition_ShouldReturnFalse() {
        DashboardModuleDummy dashboardModuleA = new DashboardModuleDummy(0);
        DashboardModuleDummy dashboardModuleB = new DashboardModuleDummy(1);

        assertNotEquals(dashboardModuleA, dashboardModuleB);
    }

    @Test
    void equals_sameClassSamePosition_ShouldReturnTrue() {
        DashboardModuleDummy dashboardModuleA = new DashboardModuleDummy(11);
        DashboardModuleDummy dashboardModuleB = new DashboardModuleDummy(11);

        assertEquals(dashboardModuleA, dashboardModuleB);
    }


}
