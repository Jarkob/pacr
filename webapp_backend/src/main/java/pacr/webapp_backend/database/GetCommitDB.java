package pacr.webapp_backend.database;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.GitBranch;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.result_management.services.IGetCommitAccess;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation for IGetCommitAccess.
 */
@Component
public class GetCommitDB extends CommitRepositoryDB implements IGetCommitAccess {
    /**
     * Creates a new instance of GetCommitDB.
     *
     * @param commitDB     is the JPA commit access interface.
     * @param repositoryDB is the JPA repository access interface.
     */
    public GetCommitDB(@NotNull CommitDB commitDB, @NotNull RepositoryDB repositoryDB) {
        super(commitDB, repositoryDB);
    }


    @Override
    public Collection<? extends ICommit> getCommitsFromRepository(int id) {
        return commitDB.findCommitsByRepository_Id(id);
    }

    @Override
    public Collection<? extends ICommit> getCommitsFromBranch(int id, String branchName) {
        if (branchName == null) {
            throw new IllegalArgumentException("branch name cannot be null");
        }

        GitRepository repository = super.repositoryDB.findById(id).orElse(null);

        if (repository == null) {
            return null;
        }

        Collection<GitBranch> branches = repository.getSelectedBranches();

        GitBranch match = null;

        for (GitBranch branch : branches) {
            if (branch.getName().equals(branchName)) {
                match = branch;
                break;
            }
        }

        if (match == null) {
            return null;
        }

        return commitDB.findCommitsByRepository_IdAndBranches(id, match);
    }

    @Override
    public Collection<? extends ICommit> getAllCommits() {
        List<GitCommit> commits = new LinkedList<>();
        commitDB.findAll().forEach(commits::add);
        return commits;
    }

    @Override
    public GitCommit getCommit(@NotNull String commitHash) {
        if (commitHash == null) {
            throw new IllegalArgumentException("commitHash cannot be null");
        }
        return commitDB.findById(commitHash).orElse(null);
    }
}
