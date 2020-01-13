package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import pacr.webapp_backend.git_tracking.Repository;
import pacr.webapp_backend.git_tracking.services.IRepositoryAccess;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;

/**
 * This is an implementation of the IRepositoryAccess interface.
 *
 * @author Pavel Zwerschke
 */
public interface RepositoryDB extends CrudRepository<Repository, Integer>, IRepositoryAccess {

    default Collection<Repository> getAllRepositories() {
        Collection<Repository> repositories = new HashSet<>();

        for (Repository repository : this.findAll()) {
            repositories.add(repository);
        }

        return repositories;
    }

    default Repository getRepository(int repositoryID) {
        return this.findById(repositoryID).orElse(null);
    }

    default int addRepository(@NotNull Repository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null.");
        }
        return this.save(repository).getId();
    }

    default void removeRepository(int repositoryID) {
        this.deleteById(repositoryID);
    }

    default void updateRepository(@NotNull Repository repository) {
        addRepository(repository);
    }

}