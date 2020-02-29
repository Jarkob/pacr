package pacr.webapp_backend.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pacr.webapp_backend.result_management.services.CommitResult;
import pacr.webapp_backend.result_management.services.IResultAccess;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This is a default implementation of the IResultAccess interface.
 */
@Component
public interface ResultDB extends PagingAndSortingRepository<CommitResult, String>, IResultAccess {
    @Override
    default Page<CommitResult> getNewestResults(Pageable pageable) {
        return this.findAllByOrderByEntryDateDesc(pageable);
    }

    @Override
    default CommitResult getNewestResult(int repositoryId) {
        return this.findFirstByRepositoryIDOrderByEntryDateDesc(repositoryId);
    }

    @Override
    default Collection<CommitResult> getResultsFromCommits(Collection<String> commitHashes) {
        Objects.requireNonNull(commitHashes);

        List<CommitResult> commitResults = new LinkedList<>();
        this.findAllById(commitHashes).forEach(commitResults::add);
        return commitResults;
    }

    @Override
    default CommitResult getResultFromCommit(String commitHash) {
        Objects.requireNonNull(commitHash);
        return this.findById(commitHash).orElse(null);
    }

    @Override
    default void saveResult(CommitResult result) {
        Objects.requireNonNull(result);
        this.save(result);
    }

    @Override
    default void deleteResults(Collection<String> commitHashes) {
        this.removeCommitResultsByCommitHashIn(commitHashes);
    }

    @Override
    default List<CommitResult> getAllResults() {
        List<CommitResult> results = new LinkedList<>();

        for (CommitResult result : this.findAll()) {
            results.add(result);
        }

        return results;
    }

    @Override
    default Page<CommitResult> getFullRepositoryResults(int repositoryId, Pageable pageable) {
        return findAllByRepositoryID(repositoryId, pageable);
    }

    @Override
    default List<CommitResult> getResultsWithComparisionCommitHash(@NotNull String commitHash) {
        Objects.requireNonNull(commitHash);

        return findAllByComparisonCommitHash(commitHash);
    }

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @param repositoryId the id of the repository.
     * @param pageable the requested page.
     * @return a page of the commit results of the repository.
     */
    Page<CommitResult> findAllByRepositoryID(int repositoryId, Pageable pageable);

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @return the requested page of the latest commit results in descending order by entry date.
     */
    Page<CommitResult> findAllByOrderByEntryDateDesc(Pageable pageable);

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @param repositoryId the id of the repository.
     * @return the newest saved result for the repository.
     */
    CommitResult findFirstByRepositoryIDOrderByEntryDateDesc(int repositoryId);

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @param comparisionCommitHash the hash of the comparision commit.
     * @return the results with the given comparision commit hash.
     */
    List<CommitResult> findAllByComparisonCommitHash(String comparisionCommitHash);

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @param commitHashes hashes of the commits whose results are meant to be deleted.
     * @return the amount of deleted results.
     */
    @Transactional
    Long removeCommitResultsByCommitHashIn(Collection<String> commitHashes);
}
