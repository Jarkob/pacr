package pacr.webapp_backend.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.services.CommitResult;
import pacr.webapp_backend.result_management.services.IResultAccess;

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
    default List<CommitResult> getNewestResults() {
        return this.findFirst100ByOrderByEntryDateDesc();
    }

    @Override
    default CommitResult getNewestResult(int repositoryId) {
        return this.findFirstByRepositoryIdOrderByEntryDateDesc(repositoryId);
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
    default void deleteResult(CommitResult result) {
        this.delete(result);
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
        return findAllByRepositoryId(repositoryId, pageable);
    }

    Page<CommitResult> findAllByRepositoryId(int repositoryId, Pageable pageable);

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @return the latest 100 commit results in descending order by entry date.
     */
    List<CommitResult> findFirst100ByOrderByEntryDateDesc();

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @param repositoryId the id of the repository.
     * @return the newest saved result for the repository.
     */
    CommitResult findFirstByRepositoryIdOrderByEntryDateDesc(int repositoryId);
}
