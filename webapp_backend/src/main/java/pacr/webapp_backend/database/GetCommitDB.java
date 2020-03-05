package pacr.webapp_backend.database;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.result_management.services.IGetCommitAccess;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
        return commitDB.findGitCommitsByRepository_Id(id);
    }

    @Override
    public List<? extends ICommit> getCommitsFromBranchTimeFrame(int repositoryId, String branchName, LocalDateTime commitDateStart, LocalDateTime commitDateEnd) {
        Objects.requireNonNull(branchName);
        Objects.requireNonNull(commitDateStart);
        Objects.requireNonNull(commitDateEnd);

        GitBranch branch = getGitBranch(repositoryId, branchName);
        if (branch == null) {
            return null;
        }

        return commitDB.findGitCommitByRepository_IdAndBranchesAndCommitDateBetween(
                repositoryId, branch, commitDateStart, commitDateEnd);
    }

    @Override
    public GitCommit getCommit(@NotNull String commitHash) {
        Objects.requireNonNull(commitHash);

        return commitDB.findById(commitHash).orElse(null);
    }

    @Nullable
    private GitBranch getGitBranch(int id, String branchName) {
        GitRepository repository = super.repositoryDB.findById(id).orElse(null);

        if (repository == null) {
            return null;
        }

        Collection<GitBranch> branches = repository.getTrackedBranches();

        GitBranch match = null;

        for (GitBranch branch : branches) {
            if (branch.getName().equals(branchName)) {
                match = branch;
                break;
            }
        }

        return match;
    }
}
