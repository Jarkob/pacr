package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventDashboardModuleTest {

    @Test
    void constructor_NoArguments_ShouldHaveInvalidState() {
        EventDashboardModule eventModule = new EventDashboardModule();

        assertThrows(IllegalStateException.class, eventModule::getPosition);
    }
}
