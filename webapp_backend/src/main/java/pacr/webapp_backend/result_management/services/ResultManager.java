package pacr.webapp_backend.result_management.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.IResultDeleter;
import pacr.webapp_backend.shared.IResultImporter;
import pacr.webapp_backend.shared.IResultSaver;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Manages benchmarking results in the system. Can save or import new results and delete old ones.
 */
@Component
public class ResultManager implements IResultDeleter, IResultImporter, IResultSaver {

    private static final Logger LOGGER = LogManager.getLogger(ResultManager.class);

    private IResultAccess resultAccess;
    private IGetCommitAccess commitAccess;
    private ResultImportSaver resultImportSaver;
    private ResultBenchmarkSaver resultBenchmarkSaver;

    /**
     * Creates a new ResultManager. Dependencies are injected.
     * @param resultAccess access to results.
     * @param commitAccess access to ICommits.
     * @param resultImportSaver Component for saving imported benchmarking results.
     * @param resultBenchmarkSaver Component for saving generated benchmarking results.
     */
    ResultManager(IResultAccess resultAccess, IGetCommitAccess commitAccess, ResultImportSaver resultImportSaver,
                  ResultBenchmarkSaver resultBenchmarkSaver) {
        this.resultAccess = resultAccess;
        this.commitAccess = commitAccess;
        this.resultImportSaver = resultImportSaver;
        this.resultBenchmarkSaver = resultBenchmarkSaver;
    }

    @Override
    public void deleteBenchmarkingResults(@NotNull Collection<String> commitHashes) {
        Objects.requireNonNull(commitHashes);

        synchronized (CommitResult.class) {
            resultAccess.deleteResults(commitHashes);
        }

        LOGGER.info("deleted {} results", commitHashes.size());
    }

    @Override
    public void importBenchmarkingResults(@NotNull Collection<IBenchmarkingResult> results) {
        Objects.requireNonNull(results);

        Map<IBenchmarkingResult, ICommit> resultsWithCommits = new HashMap<>();

        for (IBenchmarkingResult result : results) {
            ICommit commit = commitAccess.getCommit(result.getCommitHash());

            if (commit == null) {
                LOGGER.error("could not find commit with hash {} - its result will not be saved",
                        result.getCommitHash());
                continue;
            }

            resultsWithCommits.put(result, commit);
        }

        for (IBenchmarkingResult result : resultsWithCommits.keySet()) {
            ICommit commit = resultsWithCommits.get(result);
            resultImportSaver.saveResult(result, commit, getComparisonCommitHash(commit));
            updateComparisonsForChildren(result.getCommitHash());
        }

        LOGGER.info("saved results");
    }

    @Override
    public void saveBenchmarkingResults(@NotNull IBenchmarkingResult benchmarkingResult) {
        Objects.requireNonNull(benchmarkingResult);

        ICommit commit = commitAccess.getCommit(benchmarkingResult.getCommitHash());

        if (commit == null) {
            LOGGER.error("could not find commit with hash {}", benchmarkingResult.getCommitHash());
            return;
        }

        resultBenchmarkSaver.saveResult(benchmarkingResult, commit, getComparisonCommitHash(commit));
        updateComparisonsForChildren(benchmarkingResult.getCommitHash());
    }

    private String getComparisonCommitHash(ICommit commit) {
        if (commit == null) {
            return null;
        }

        Collection<String> parentHashes = commit.getParentHashes();
        Collection<ICommit> parents = new LinkedList<>();

        for (String parentHash : parentHashes) {
            ICommit parent = commitAccess.getCommit(parentHash);
            parents.add(parent);
        }

        if (parents.isEmpty()) {
            return null;
        }

        ICommit comparisonCommit = getCommitLatestCommitDate(parents);

        return comparisonCommit.getCommitHash();
    }

    private ICommit getCommitLatestCommitDate(Collection<? extends ICommit> commits) {
        LocalDateTime latestTime = LocalDateTime.MIN;
        ICommit latestCommit = null;
        for (ICommit commit : commits) {
            if (commit.getCommitDate().isAfter(latestTime)) {
                latestCommit = commit;
                latestTime = commit.getCommitDate();
            }
        }
        return latestCommit;
    }

    private void updateComparisonsForChildren(String commitHash) {
        List<CommitResult> resultsToUpdate = resultAccess.getResultsWithComparisionCommitHash(commitHash);

        CommitResult comparisonResult = resultAccess.getResultFromCommit(commitHash);
        if (comparisonResult == null) {
            return;
        }

        for (CommitResult resultToUpdate : resultsToUpdate) {
            if (!resultToUpdate.isCompared()) {
                resultToUpdate.setCompared(true);

                for (BenchmarkResult benchmarkResult : resultToUpdate.getBenchmarkResults()) {
                    BenchmarkResult comparisonBenchmarkResult = comparisonResult.getBenchmarks()
                            .get(benchmarkResult.getName());

                    if (comparisonBenchmarkResult != null) {
                        for (BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                            BenchmarkPropertyResult comparisonPropertyResult = comparisonBenchmarkResult
                                    .getBenchmarkProperties().get(propertyResult.getName());
                            StatisticalCalculator.compare(propertyResult, comparisonPropertyResult);
                        }
                    }
                }

                synchronized (CommitResult.class) {
                    resultAccess.saveResult(resultToUpdate);
                }

                LOGGER.info("updated result for commit {} with new comparision data",
                        resultToUpdate.getCommitHash());
            }
        }
    }
}
