package pacr.webapp_backend.database;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.import_export.servies.IExportRepositoryAccess;
import pacr.webapp_backend.shared.IRepository;

/**
 * This is a crud repository for GitRepository.
 *
 * @author Pavel Zwerschke
 */
public interface RepositoryDB extends CrudRepository<GitRepository, Integer>, IExportRepositoryAccess {

    default IRepository findGitRepositoryById(int id) {
        GitRepository repository = this.findById(id).orElse(null);

        if (repository != null) {
            return new IRepository() {
                @Override
                public String getName() {
                    return repository.getName();
                }

                @Override
                public String getPullURL() {
                    return repository.getPullURL();
                }

                @Override
                public Set<String> getTrackedBranches() {
                    Set<String> trackedBranches = new HashSet<>();

                    for (GitBranch branch : repository.getTrackedBranches()) {
                        trackedBranches.add(branch.getName());
                    }

                    return trackedBranches;
                }
            };
        }

        return null;
    }

}