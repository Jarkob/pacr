package pacr.webapp_backend.database;

/**
 * Gets thrown when a repository is not stored yet but should be.
 *
 * @author Pavel Zwerschke
 */
public class RepositoryNotStoredException extends RuntimeException {

    public RepositoryNotStoredException() {
    }

    public RepositoryNotStoredException(String message) {
        super(message);
    }

    public RepositoryNotStoredException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryNotStoredException(Throwable cause) {
        super(cause);
    }
}
