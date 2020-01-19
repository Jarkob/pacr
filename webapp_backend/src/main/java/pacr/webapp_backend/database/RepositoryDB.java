package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

/**
 * This is a crud repository for GitRepository.
 *
 * @author Pavel Zwerschke
 */
public interface RepositoryDB extends CrudRepository<GitRepository, Integer>, IExportRepositoryAccess {

}