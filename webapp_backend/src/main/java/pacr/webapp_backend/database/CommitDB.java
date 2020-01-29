package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;

import java.util.Collection;

/**
 * This an implementation of the ICommitAccess Interface.
 *
 * @author Pavel Zwerschke
 */
@Component
public interface CommitDB extends CrudRepository<GitCommit, String> {

    Collection<GitCommit> findGitCommitsByRepository_Id(int repositoryID);

    @Transactional
    void removeGitCommitsByRepository_Id(int repositoryID);

    Collection<GitCommit> findGitCommitsByRepository_IdAndBranches(int repositoryID, GitBranch branch);

}
