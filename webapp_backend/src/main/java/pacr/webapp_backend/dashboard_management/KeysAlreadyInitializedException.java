package pacr.webapp_backend.dashboard_management;

/**
 * Thrown, when existing keys of a dashboard are attempted to be overwritten
 *
 * @author Benedikt Hahn
 */
public class KeysAlreadyInitializedException extends RuntimeException {
    KeysAlreadyInitializedException(final String message) {
        super(message);
    }
}
