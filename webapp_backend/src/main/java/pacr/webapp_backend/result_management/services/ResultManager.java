package pacr.webapp_backend.result_management.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.IResultDeleter;
import pacr.webapp_backend.shared.IResultImporter;
import pacr.webapp_backend.shared.IResultSaver;

import javax.validation.constraints.NotNull;

/**
 * Manages benchmarking results in the system. Can save or import new results and delete old ones.
 */
@Component
public class ResultManager implements IResultDeleter, IResultImporter, IResultSaver {

    private static final Logger LOGGER = LogManager.getLogger(ResultManager.class);

    private final IResultAccess resultAccess;
    private final IGetCommitAccess commitAccess;
    private final ResultImportSaver resultImportSaver;
    private final ResultBenchmarkSaver resultBenchmarkSaver;

    /**
     * Creates a new ResultManager. Dependencies are injected.
     * @param resultAccess access to results.
     * @param commitAccess access to ICommits.
     * @param resultImportSaver Component for saving imported benchmarking results.
     * @param resultBenchmarkSaver Component for saving generated benchmarking results.
     */
    ResultManager(final IResultAccess resultAccess, final IGetCommitAccess commitAccess,
                  final ResultImportSaver resultImportSaver,
                  final ResultBenchmarkSaver resultBenchmarkSaver) {
        this.resultAccess = resultAccess;
        this.commitAccess = commitAccess;
        this.resultImportSaver = resultImportSaver;
        this.resultBenchmarkSaver = resultBenchmarkSaver;
    }

    @Override
    public void deleteBenchmarkingResults(@NotNull final Collection<String> commitHashes) {
        Objects.requireNonNull(commitHashes);

        synchronized (CommitResult.class) {
            resultAccess.deleteResults(commitHashes);
        }

        LOGGER.info("Deleted {} results", commitHashes.size());
    }

    @Override
    public void importBenchmarkingResults(@NotNull final Collection<IBenchmarkingResult> results) {
        Objects.requireNonNull(results);

        final Map<IBenchmarkingResult, ICommit> resultsWithCommits = new HashMap<>();

        for (final IBenchmarkingResult result : results) {
            final ICommit commit = commitAccess.getCommit(result.getCommitHash());

            if (commit == null) {
                LOGGER.error("could not find commit with hash {} - its result will not be saved",
                        result.getCommitHash());
                continue;
            }

            resultsWithCommits.put(result, commit);
        }

        for (final Map.Entry<IBenchmarkingResult, ICommit> entry : resultsWithCommits.entrySet()) {
            final IBenchmarkingResult result = entry.getKey();
            final ICommit commit = entry.getValue();
            resultImportSaver.saveResult(result, commit, getComparisonCommitHash(commit));
            updateComparisonsForChildren(result.getCommitHash());
        }

        LOGGER.info("saved results");
    }

    @Override
    public void saveBenchmarkingResults(@NotNull final IBenchmarkingResult benchmarkingResult) {
        Objects.requireNonNull(benchmarkingResult);

        final ICommit commit = commitAccess.getCommit(benchmarkingResult.getCommitHash());

        if (commit == null) {
            LOGGER.error("could not find commit with hash {}", benchmarkingResult.getCommitHash());
            return;
        }

        resultBenchmarkSaver.saveResult(benchmarkingResult, commit, getComparisonCommitHash(commit));
        updateComparisonsForChildren(benchmarkingResult.getCommitHash());
    }

    @Nullable
    private String getComparisonCommitHash(final ICommit commit) {
        if (commit == null) {
            return null;
        }

        final Collection<String> parentHashes = commit.getParentHashes();
        final Collection<ICommit> parents = new LinkedList<>();

        for (final String parentHash : parentHashes) {
            final ICommit parent = commitAccess.getCommit(parentHash);
            parents.add(parent);
        }

        if (parents.isEmpty()) {
            return null;
        }

        final ICommit comparisonCommit = getCommitLatestCommitDate(parents);

        return comparisonCommit.getCommitHash();
    }

    private ICommit getCommitLatestCommitDate(final Collection<? extends ICommit> commits) {
        LocalDateTime latestTime = LocalDateTime.MIN;
        ICommit latestCommit = null;
        for (final ICommit commit : commits) {
            if (commit.getCommitDate().isAfter(latestTime)) {
                latestCommit = commit;
                latestTime = commit.getCommitDate();
            }
        }
        return latestCommit;
    }

    private void updateComparisonsForChildren(final String commitHash) {

        final CommitResult comparisonResult = resultAccess.getResultFromCommit(commitHash);
        if (comparisonResult == null) {
            return;
        }

        final List<CommitResult> resultsToUpdate = resultAccess.getResultsWithComparisionCommitHash(commitHash);

        for (final CommitResult resultToUpdate : resultsToUpdate) {
            if (!resultToUpdate.isCompared()) {
                resultToUpdate.setCompared(true);
                final Map<String, BenchmarkResult> comparisonBenchmarkResults = comparisonResult.getBenchmarks();

                for (final BenchmarkResult benchmarkResult : resultToUpdate.getBenchmarkResults()) {
                    final BenchmarkResult comparisonBenchmarkResult = comparisonBenchmarkResults
                            .get(benchmarkResult.getName());

                    if (comparisonBenchmarkResult != null) {
                        final Map<String, BenchmarkPropertyResult> comparisonPropertyResults = comparisonBenchmarkResult
                                .getBenchmarkProperties();

                        for (final BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                            final BenchmarkPropertyResult comparisonPropertyResult = comparisonPropertyResults
                                    .get(propertyResult.getName());
                            StatisticalCalculator.compare(propertyResult, comparisonPropertyResult);
                        }
                    }
                }

                resultToUpdate.updateSignificance();

                synchronized (CommitResult.class) {
                    resultAccess.saveResult(resultToUpdate);
                }

                LOGGER.info("updated result for commit {} with new comparision data",
                        resultToUpdate.getCommitHash());
            }
        }
    }
}
