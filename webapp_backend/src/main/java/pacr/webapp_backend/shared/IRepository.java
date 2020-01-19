package pacr.webapp_backend.shared;

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
