package pacr.webapp_backend.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * Handles behavior of the console.
 */
@ShellComponent
public class ShellConfigurator {

    private static final String LOGGER_NAME = "pacr.webapp_backend";
    private static final String APPENDER_NAME = "ConsoleAppender";
    private static final String OFF_MESSAGE = "log output off";
    private static final String ON_MESSAGE = "log output on";

    private static boolean logOutput = true;

    /**
     * Toggles whether logs are displayed on the console. Regardless of this setting, they are always saved to a log
     * file.
     * @return a message describing, whether the output is on or off after execution.
     */
    @ShellMethod("toggles whether logs are displayed on the console.")
    public synchronized String toggleLogOutput() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        final LoggerConfig appLogger = config.getLoggers().get(LOGGER_NAME);
        final LoggerConfig rootLogger = config.getRootLogger();

        final String output;

        if (logOutput) {
            appLogger.removeAppender(APPENDER_NAME);
            rootLogger.removeAppender(APPENDER_NAME);

            logOutput = false;
            output = OFF_MESSAGE;
        } else {
            final Appender consoleAppender = config.getAppender(APPENDER_NAME);

            appLogger.addAppender(consoleAppender, appLogger.getLevel(), appLogger.getFilter());
            rootLogger.addAppender(consoleAppender, rootLogger.getLevel(), rootLogger.getFilter());

            logOutput = true;
            output = ON_MESSAGE;
        }
        ctx.updateLoggers();

        return output;
    }
}
