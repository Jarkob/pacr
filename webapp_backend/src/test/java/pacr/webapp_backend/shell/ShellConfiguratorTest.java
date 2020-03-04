package pacr.webapp_backend.shell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShellConfiguratorTest {
    private static final String LOG_ON = "log output on";
    private static final String LOG_OFF = "log output off";

    @Test
    void toggleLogOutput_logIsOn_shouldTurnOff() {
        ShellConfigurator configurator = new ShellConfigurator();

        String output = configurator.toggleLogOutput();

        assertEquals(LOG_OFF, output);
    }

    @Test
    void toggleLogOutput_logIsOff_shouldTurnOn() {
        ShellConfigurator configurator = new ShellConfigurator();

        configurator.toggleLogOutput();
        String output = configurator.toggleLogOutput();

        assertEquals(LOG_ON, output);
    }
}
