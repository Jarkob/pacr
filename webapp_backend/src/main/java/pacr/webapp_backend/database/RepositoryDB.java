package pacr.webapp_backend.database;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.import_export.servies.IExportRepositoryAccess;
import pacr.webapp_backend.shared.IRepository;

/**
 * This is a crud repository for GitRepository.
 *
 * @author Pavel Zwerschke
 */
public interface RepositoryDB extends CrudRepository<GitRepository, Integer>, IExportRepositoryAccess {

    @Override
    default IRepository findGitRepositoryById(int id) {
        return this.findById(id).orElse(null);
    }

    /**
     * @return all saved repositories ordered by their name.
     */
    List<GitRepository> findAllByOrderByName();

}