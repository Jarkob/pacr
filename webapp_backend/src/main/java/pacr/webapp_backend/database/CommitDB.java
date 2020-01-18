package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.GitBranch;
import pacr.webapp_backend.git_tracking.GitCommit;

import java.util.Collection;

/**
 * This an implementation of the ICommitAccess Interface.
 *
 * @author Pavel Zwerschke
 */
@Component
public interface CommitDB extends CrudRepository<GitCommit, String> {

    Collection<GitCommit> findCommitsByRepository_Id(int repositoryID);

    Collection<GitCommit> findCommitsByRepository_IdAndBranch(int repositoryID, GitBranch branch);

}
