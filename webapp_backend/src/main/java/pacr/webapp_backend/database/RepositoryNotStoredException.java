package pacr.webapp_backend.database;

/**
 * Gets thrown when a repository is not stored yet but should be.
 *
 * @author Pavel Zwerschke
 */
public class RepositoryNotStoredException extends RuntimeException {

    public RepositoryNotStoredException(String message) {
        super(message);
    }

}
