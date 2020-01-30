package pacr.webapp_backend.result_management.services;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.IResultDeleter;
import pacr.webapp_backend.shared.IResultImporter;
import pacr.webapp_backend.shared.IResultSaver;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    public void deleteBenchmarkingResults(@NotNull String commitHash) {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        synchronized (CommitResult.class) {
            CommitResult result = resultAccess.getResultFromCommit(commitHash);

            if (result != null) {
                resultAccess.deleteResult(result);
            }
        }
    }

    @Override
    public void deleteAllResultsForRepository(int repositoryID) {
        Collection<? extends ICommit> commits = commitAccess.getCommitsFromRepository(repositoryID);

        if (commits == null) {
            return;
        }

        for (ICommit commit : commits) {
            deleteBenchmarkingResults(commit.getCommitHash());
        }
    }

    @Override
    public void importBenchmarkingResults(@NotNull Collection<IBenchmarkingResult> results) {
        Objects.requireNonNull(results);

        Map<IBenchmarkingResult, ICommit> resultsWithCommits = new HashMap<>();

        for (IBenchmarkingResult result : results) {
            ICommit commit = commitAccess.getCommit(result.getCommitHash());

            if (commit == null) {
                throw new IllegalArgumentException("could not find commit with hash " + result.getCommitHash());
            }

            resultsWithCommits.put(result, commit);
        }

        for (IBenchmarkingResult result : results) {
            resultImportSaver.saveResult(result, resultsWithCommits.get(result), null);
        }
    }

    @Override
    public void saveBenchmarkingResults(@NotNull IBenchmarkingResult benchmarkingResult) {
        Objects.requireNonNull(benchmarkingResult);

        ICommit commit = commitAccess.getCommit(benchmarkingResult.getCommitHash());

        if (commit == null) {
            throw new IllegalArgumentException("could not find commit with hash " + benchmarkingResult.getCommitHash());
        }

        String comparisonCommitHash = null;

        ICommit comparisonCommit = getComparisonCommit(commit);

        if (comparisonCommit != null) {
            comparisonCommitHash = comparisonCommit.getCommitHash();
        }

        resultBenchmarkSaver.saveResult(benchmarkingResult, commit, comparisonCommitHash);
    }

    private ICommit getComparisonCommit(ICommit commit) {
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

        return getCommitLatestCommitDate(parents);
    }

    private ICommit getCommitLatestCommitDate(Collection<? extends ICommit> commits) {
        LocalDateTime latestTime = LocalDateTime.MIN;
        ICommit latestCommit = null;
        for (ICommit commit : commits) {
            if (commit.getCommitDate().compareTo(latestTime) >= 0) {
                latestCommit = commit;
                latestTime = commit.getCommitDate();
            }
        }
        return latestCommit;
    }
}
