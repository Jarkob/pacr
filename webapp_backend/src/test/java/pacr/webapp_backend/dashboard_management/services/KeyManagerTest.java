package pacr.webapp_backend.dashboard_management.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.dashboard_management.Dashboard;

public class KeyManagerTest {

    @Test
    void generateEditKey_normalDashboard_ShouldChangeEditKey() {
        Dashboard dashboard = new Dashboard("test");

        String oldEditKey = dashboard.getEditKey();

        KeyManager.generateEditKey(dashboard);

        assertNotEquals(oldEditKey, dashboard.getEditKey());
    }

    @Test
    void generateEditKey_nullDashboard_ShouldThrowException() {
        Dashboard dashboard = null;

        assertThrows(IllegalArgumentException.class, () -> KeyManager.generateEditKey(dashboard));

    }

    @Test
    void generateViewKey_normalDashboard_ShouldChangeViewKey() {
        Dashboard dashboard = new Dashboard("test");

        String oldViewKey = dashboard.getViewKey();

        KeyManager.generateViewKey(dashboard);

        assertNotEquals(oldViewKey, dashboard.getViewKey());
    }

    @Test
    void generateViewKey_nullDashboard_ShouldThrowException() {
        Dashboard dashboard = null;

        assertThrows(IllegalArgumentException.class, () -> KeyManager.generateViewKey(dashboard));

    }
}
