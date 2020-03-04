package pacr.webapp_backend.dashboard_management;

/**
 * This exception is thrown, to indicate that keys in a dashboard, were attempted to be overwritten.
 */
public class KeysAlreadyInitializedException extends RuntimeException {
    KeysAlreadyInitializedException(final String message) {
        super(message);
    }
}
