package pacr.webapp_backend.git_tracking.services.git;

/**
 * @author Pavel Zwerschke
 */
public class PullFromRepositoryException extends Exception {

    public PullFromRepositoryException(final String message) {
        super(message);
    }

}
