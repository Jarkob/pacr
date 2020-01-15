package pacr.webapp_backend.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public interface CommitDB extends CrudRepository<Commit, String>, ICommitAccess {

    default void addCommit(Commit commit) {
        if (!commit.repositoryIsInDatabase()) {
            throw new RepositoryNotStoredException("The repository of the commit must be stored in the database "
                    + "before this commit is being stored in the database.");
        }
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

    default Commit getCommit(String commitHash) {
        return findById(commitHash).orElse(null);
    }

    default void removeCommit(String commitHash) {
        this.deleteById(commitHash);
    }

}
