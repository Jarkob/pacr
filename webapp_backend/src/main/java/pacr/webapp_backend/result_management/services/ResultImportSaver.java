package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Saves benchmarking results that are imported into the pacr system. Does not update other components
 */
@Component
public class ResultImportSaver extends ResultSaver {

    /**
     * Creates a ResultImportSaver with access to results and a benchmark manager.
     * @param resultAccess access to results in storage.
     * @param benchmarkManager a benchmark manager to add newly detected benchmarks.
     */
    ResultImportSaver(final IResultAccess resultAccess, final BenchmarkManager benchmarkManager) {
        super(resultAccess, benchmarkManager);
    }

    /**
     * Does nothing (except throw exceptions for wrong input).
     * No components get updated for results that are being imported.
     */
    @Override
    void updateOtherComponents(@NotNull final CommitResult result, @NotNull final ICommit commit,
                               @Nullable final String comparisonCommitHash) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(commit);
    }
}
