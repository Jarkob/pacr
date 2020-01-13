package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.Commit;
import pacr.webapp_backend.git_tracking.services.ICommitAccess;

import java.util.Collection;
import java.util.HashSet;

/**
 * This an implementation of the ICommitAccess Interface.
 *
 * @author Pavel Zwerschke
 */
@Component
public interface CommitDB extends CrudRepository<Commit, Integer>, ICommitAccess {

    default void addCommit(Commit commit) {
        this.save(commit);
    }

    default Collection<Commit> getAllCommits(int repositoryID) {
        Collection<Commit> commits = new HashSet<>();

        for (Commit commit : this.findAll()) {
            if (commit.getRepositoryID() == repositoryID) {
                commits.add(commit);
            }
        }

        return commits;
    }

}
