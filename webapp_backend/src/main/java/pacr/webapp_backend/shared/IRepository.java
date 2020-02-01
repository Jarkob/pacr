package pacr.webapp_backend.shared;

/**
 * An interface for a git repository.
 */
public interface IRepository {

    /**
     * Returns the name of the repository.
     * @return name
     */
    String getName();

    /**
     * Returns the pull URL for the repository.
     * @return pull URL
     */
    String getPullURL();

}
