package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.services.IResultAccess;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a default implementation of the IResultAccess interface.
 */
@Component
public interface ResultDB extends CrudRepository<CommitResult, String>, IResultAccess {
    @Override
    default List<CommitResult> getNewestResults() {
        return this.findFirst100ByOrderByEntryDateDesc();
    }

    @Override
    default Collection<CommitResult> getResultsFromCommits(Collection<String> commitHashes) {
        List<CommitResult> commitResults = new LinkedList<>();
        this.findAllById(commitHashes).forEach(commitResults::add);
        return commitResults;
    }

    @Override
    default CommitResult getResultFromCommit(String commitHash) {
        return this.findById(commitHash).orElse(null);
    }

    @Override
    default void saveResult(CommitResult result) {
        this.save(result);
    }

    @Override
    default void deleteResult(CommitResult result) {
        this.delete(result);
    }

    /**
     * This is a method that is automatically created by jpa based on its method name.
     * @return the latest 100 commit results in descending order by entry date.
     */
    List<CommitResult> findFirst100ByOrderByEntryDateDesc();
}
