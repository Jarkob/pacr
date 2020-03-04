package pacr.webapp_backend.dashboard_management;

public class KeysAlreadyInitializedException extends RuntimeException {
    KeysAlreadyInitializedException(final String message) {
        super(message);
    }
}
