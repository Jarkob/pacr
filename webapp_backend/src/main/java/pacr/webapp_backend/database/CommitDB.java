package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.Commit;

import java.util.Collection;

/**
 * This an implementation of the ICommitAccess Interface.
 *
 * @author Pavel Zwerschke
 */
@Component
public interface CommitDB extends CrudRepository<Commit, String> {

    Collection<Commit> findCommitsByRepository_Id(int repositoryID);

}
