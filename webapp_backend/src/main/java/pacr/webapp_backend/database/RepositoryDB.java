package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import pacr.webapp_backend.git_tracking.GitRepository;

/**
 * This is an implementation of the IRepositoryAccess interface.
 *
 * @author Pavel Zwerschke
 */
public interface RepositoryDB extends CrudRepository<GitRepository, Integer> {
}