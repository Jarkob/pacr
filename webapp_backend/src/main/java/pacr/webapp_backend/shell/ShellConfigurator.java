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

    private static boolean logOutput = true;

    /**
     * Toggles whether logs are displayed on the console. Regardless of this setting, they are always saved to a log
     * file.
     */
    @ShellMethod("toggles whether logs are displayed on the console.")
    public void toggleLogOutput() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig appLogger = config.getLoggers().get(LOGGER_NAME);
        LoggerConfig rootLogger = config.getRootLogger();

        if (logOutput) {
            appLogger.removeAppender(APPENDER_NAME);
            rootLogger.removeAppender(APPENDER_NAME);

            logOutput = false;
        } else {
            Appender consoleAppender = config.getAppender(APPENDER_NAME);

            appLogger.addAppender(consoleAppender, appLogger.getLevel(), appLogger.getFilter());
            rootLogger.addAppender(consoleAppender, rootLogger.getLevel(), rootLogger.getFilter());

            logOutput = true;
        }

        ctx.updateLoggers();
    }
}
