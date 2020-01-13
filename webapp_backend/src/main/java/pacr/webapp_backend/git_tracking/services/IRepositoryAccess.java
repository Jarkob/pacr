package pacr.webapp_backend.git_tracking.services;

import pacr.webapp_backend.git_tracking.Repository;

import java.util.Collection;

/**
 * Represents the interface for storing and accessing
 * the data for Git Tracking.
 *
 * @author Pavel Zwerschke
 */
public interface IRepositoryAccess {

    /**
     * Returns all repositories.
     * @return repositories.
     */
    Collection<Repository> getAllRepositories();

    /**
     * Finds a repository by its repositoryID.
     * @param repositoryID is the repositoryID.
     * @return repository if it was found or else null.
     */
    Repository getRepository(int repositoryID);

    /**
     * Stores a repository.
     * @param repository is the repository being stored.
     * @return the ID of the repository.
     */
    int addRepository(Repository repository);

    /**
     * Deletes a repository.
     * @param repositoryID is the ID of the repository being deleted.
     */
    void removeRepository(int repositoryID);

    /**
     * Updates a repository.
     * @param repository is the repository being updated.
     */
    void updateRepository(Repository repository);

}
