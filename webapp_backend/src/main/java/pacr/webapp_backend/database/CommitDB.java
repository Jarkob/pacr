package pacr.webapp_backend.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This an implementation of the ICommitAccess Interface.
 *
 * @author Pavel Zwerschke
 */
@Component
public interface CommitDB extends PagingAndSortingRepository<GitCommit, String> {

    Collection<GitCommit> findGitCommitsByRepository_Id(int repositoryID);

    @Transactional
    void removeGitCommitsByRepository_Id(int repositoryID);

    @Transactional
    void removeGitCommitsByCommitHashIn(Collection<String> commitHashes);

    Set<GitCommit> findGitCommitsByCommitHashIn(Set<String> commitHash);

    Collection<GitCommit> findGitCommitsByRepository_IdAndBranches(int repositoryID, GitBranch branch);

    List<GitCommit> findGitCommitByRepository_IdAndBranchesAndCommitDateBetween(
            int repositoryID, GitBranch branch, LocalDateTime commitDateStart, LocalDateTime commitDateEnd);

    Page<GitCommit> findAllByRepository_Id(int repositoryID, Pageable pageable);
}
