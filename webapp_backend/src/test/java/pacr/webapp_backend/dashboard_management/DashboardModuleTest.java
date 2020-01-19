package pacr.webapp_backend.dashboard_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DashboardModuleTest {



    @Test
    void setPosition_ValidExtremePositions_ShouldNotThrowException_ShouldSetCorrectValues() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertDoesNotThrow(() -> dashboardModule.setPosition(DashboardModule.MAX_POSITION));
        assertEquals(dashboardModule.getPosition(), DashboardModule.MAX_POSITION);

        assertDoesNotThrow(() -> dashboardModule.setPosition(DashboardModule.MIN_POSITION));
        assertEquals(dashboardModule.getPosition(), DashboardModule.MIN_POSITION);
    }


    @Test
    void setPosition_TooLowPosition_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertThrows(IllegalArgumentException.class,
                () -> dashboardModule.setPosition(DashboardModule.MIN_POSITION - 1));
    }

    @Test
    void setPosition_TooHighPosition_ShouldThrowException() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        assertThrows(IllegalArgumentException.class,
                () -> dashboardModule.setPosition(DashboardModule.MAX_POSITION + 1));
    }

    @Test
    void getPosition_ValidPositions_ShouldSetCorrectValues() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy();

        dashboardModule.setPosition(7);
        assertEquals(7, dashboardModule.getPosition());

        dashboardModule.setPosition(DashboardModule.MAX_POSITION);
        assertEquals(dashboardModule.getPosition(), DashboardModule.MAX_POSITION);

        dashboardModule.setPosition(DashboardModule.MIN_POSITION);
        assertEquals(dashboardModule.getPosition(), DashboardModule.MIN_POSITION);
    }

    @Test
    void getPosition_ValidPositionFromConstructor_ShouldSetCorrectValues() {
        DashboardModuleDummy dashboardModule = new DashboardModuleDummy(3);
        assertEquals(3, dashboardModule.getPosition());

        dashboardModule = new DashboardModuleDummy(DashboardModule.MIN_POSITION);
        assertEquals(DashboardModule.MIN_POSITION, dashboardModule.getPosition());

        dashboardModule = new DashboardModuleDummy(DashboardModule.MAX_POSITION);
        assertEquals(DashboardModule.MAX_POSITION, dashboardModule.getPosition());
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
