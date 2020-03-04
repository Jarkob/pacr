package pacr.webapp_backend.git_tracking.services.git;

/**
 * @author Pavel Zwerschke
 */
public class PullFromRepositoryException extends Exception { //todo


    public PullFromRepositoryException() {
    }

    public PullFromRepositoryException(String message) {
        super(message);
    }

    public PullFromRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PullFromRepositoryException(Throwable cause) {
        super(cause);
    }
}
