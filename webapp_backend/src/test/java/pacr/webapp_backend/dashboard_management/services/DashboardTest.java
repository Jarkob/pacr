package pacr.webapp_backend.dashboard_management.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.dashboard_management.services.Dashboard;
import pacr.webapp_backend.dashboard_management.services.DashboardModule;

public class DashboardTest {


    @Test
    void removeModuleInvalidPosition() {
        Dashboard dashboard = new Dashboard("test");
        DashboardModule dashboardModule = new DashboardModule() {
            @Override
            void setPosition(int position) {
                super.setPosition(position);
            }
        };

        dashboardModule.setPosition(6);
        dashboard.addModule(dashboardModule);
        assertFalse(dashboard.removeModule(7));
    }
}
