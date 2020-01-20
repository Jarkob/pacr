package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueueDashboardModuleTest {

    @Test
    void constructor_NoArguments_ShouldHaveInvalidState() {
        QueueDashboardModule queueModule = new QueueDashboardModule();

        assertThrows(IllegalStateException.class, queueModule::getPosition);
    }
}
