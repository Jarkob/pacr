package pacr.webapp_backend.result_management.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.git_tracking.services.git.GitHandler;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ICommitBenchmarkedChecker;
import pacr.webapp_backend.shared.INewestResult;
import pacr.webapp_backend.shared.IObserver;
import pacr.webapp_backend.shared.IResultExporter;

/**
 * Gets benchmarking results from repository, branches or single commits from the database and updates observers about
 * newly saved results (that weren't imported from outside the system).
 */
@Component
public class ResultGetter implements ICommitBenchmarkedChecker, INewestResult, IResultExporter {

    private static final Logger LOGGER = LogManager.getLogger(ResultGetter.class);

    private IGetCommitAccess commitAccess;
    private IResultAccess resultAccess;
    private OutputBuilder outputBuilder;

    private Set<IObserver> observers;

    /**
     * Creates a new ResultGetter. Dependencies are injected.
     * @param commitAccess access to commits.
     * @param resultAccess access to results.
     * @param outputBuilder builder for output.
     */
    public ResultGetter(IGetCommitAccess commitAccess, IResultAccess resultAccess, OutputBuilder outputBuilder) {
        this.commitAccess = commitAccess;
        this.resultAccess = resultAccess;
        this.outputBuilder = outputBuilder;
        this.observers = new HashSet<>();
    }

    /**
     * Gets the benchmarking result of a commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @return the benchmarking result (or just the commit data if no result was found for the commit).
     */
    public OutputBenchmarkingResult getCommitResult(@NotNull String commitHash) {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        ICommit commit = commitAccess.getCommit(commitHash);

        if (commit == null) {
            throw new NoSuchElementException("no commit with hash " + commitHash + " was found.");
        }

        CommitResult result = resultAccess.getResultFromCommit(commitHash);

        if (result == null) {
            return outputBuilder.buildDetailOutput(commit);
        }

        return outputBuilder.buildDetailOutput(commit, result);
    }

    /**
     * Gets the requested page of all the results of the commits of a repository.
     * @param repositoryId the id of the repository
     * @param pageable the requested page and sort. Cannot be null.
     * @return the page of detailed results with commit information.
     */
    public Page<OutputBenchmarkingResult> getFullRepositoryResults(int repositoryId, Pageable pageable) {
        Objects.requireNonNull(pageable);

        Page<CommitResult> results = resultAccess.getFullRepositoryResults(repositoryId, pageable);

        List<OutputBenchmarkingResult> outputBenchmarkingResults = new LinkedList<>();

        for (CommitResult result : results) {
            ICommit commit = commitAccess.getCommit(result.getCommitHash());
            OutputBenchmarkingResult outputResult = outputBuilder.buildDetailOutput(commit, result);
            outputBenchmarkingResults.add(outputResult);
        }

        return new PageImpl<>(outputBenchmarkingResults, pageable, results.getTotalElements());
    }

    /**
     * Gets all benchmarking results for a repository with measurements for a specific benchmark.
     * @param repositoryId the id of the repository.
     * @param benchmarkId the id of the benchmark.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is being
     *         omitted)
     */
    public HashMap<String, DiagramOutputResult> getBenchmarkResults(int repositoryId, int benchmarkId) {
        Collection<? extends ICommit> commitsFromRepository = commitAccess.getCommitsFromRepository(repositoryId);
        return commitsToDiagramResults(commitsFromRepository, benchmarkId);
    }

    /**
     * Gets a subset of the benchmarking results for a branch with measurements for a specific benchmark.
     * @param benchmarkId the id of the benchmark.
     * @param repositoryId the id of the repository.
     * @param branch the name of the branch. Cannot be null.
     * @param page the number of the requested page.
     * @param size the size of the page.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is being
     *         omitted).
     */
    public HashMap<String, DiagramOutputResult> getBenchmarkResultsSubset(int benchmarkId, int repositoryId,
                                                                    @NotNull String branch, int page, int size) {
        Objects.requireNonNull(branch);

        Page<? extends ICommit> commitsFromBranchPage =
                commitAccess.getCommitsFromBranch(repositoryId, branch, page, size);
        List<? extends ICommit> commitsFromBranch = commitsFromBranchPage.getContent();

        return commitsToDiagramResults(commitsFromBranch, benchmarkId);
    }

    /**
     * Gets the newest saved results.
     * @param pageable the requested page.
     * @return the results.
     */
    public List<CommitHistoryItem> getNewestResults(Pageable pageable) {
        List<CommitResult> results = resultAccess.getNewestResults(pageable);

        return resultsToHistoryItems(results);
    }

    /**
     * Gets all measurements that were made for a benchmark property for a commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @param benchmarkId the id of the benchmark of the property.
     * @param propertyName the name of the property. Cannot be null, empty or blank.
     * @return the measurements for this property and commit.
     */
    public List<Double> getMeasurementsOfPropertyForCommit(@NotNull String commitHash,
                                                           int benchmarkId,
                                                           @NotNull String propertyName) {
        if (!StringUtils.hasText(commitHash) || !StringUtils.hasText(propertyName)) {
            throw new IllegalArgumentException("input cannot be null, empty or blank");
        }

        CommitResult result = resultAccess.getResultFromCommit(commitHash);
        if (result == null) {
            LOGGER.error("no result found for commit {}", commitHash);
            return new LinkedList<>();
        }

        for (BenchmarkResult benchmarkResult : result.getBenchmarkResults()) {

            if (benchmarkResult.getBenchmark().getId() == benchmarkId) {
                for (BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                    if (propertyResult.getName().equals(propertyName)) {
                        return propertyResult.getResults();
                    }
                }
                break;
            }

        }

        LOGGER.error("property {} found for commit {}", propertyName, commitHash);
        return new LinkedList<>();
    }

    @Override
    public IBenchmarkingResult getNewestResult(int repositoryID) {
        return resultAccess.getNewestResult(repositoryID);
    }

    @Override
    public List<? extends IBenchmarkingResult> exportAllBenchmarkingResults() {
        return resultAccess.getAllResults();
    }

    @Override
    public boolean isCommitBenchmarked(@NotNull String commitHash) {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        return (resultAccess.getResultFromCommit(commitHash) != null);
    }

    @Override
    public void subscribe(@NotNull IObserver observer) {
        Objects.requireNonNull(observer);
        observers.add(observer);
    }

    @Override
    public void unsubscribe(@NotNull IObserver observer) {
        Objects.requireNonNull(observer);
        observers.remove(observer);
    }

    @Override
    public void updateAll() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }

    private HashMap<String, DiagramOutputResult> commitsToDiagramResults(Collection<? extends ICommit> commits,
                                                              int benchmarkId) {
        Collection<String> hashes = new LinkedList<>();
        for (ICommit commit : commits) {
            hashes.add(commit.getCommitHash());
        }

        Map<String, CommitResult> results = getResultsMap(hashes);

        HashMap<String, DiagramOutputResult> outputResults = new HashMap<>();

        for (ICommit commit : commits) {
            CommitResult resultForCommit = results.get(commit.getCommitHash());

            DiagramOutputResult outputResult = null;

            if (resultForCommit != null) {
                outputResult = outputBuilder.buildDiagramOutput(commit, resultForCommit, benchmarkId);
            } else {
                outputResult = outputBuilder.buildDiagramOutput(commit);
            }

            outputResults.put(commit.getCommitHash(), outputResult);
        }

        return outputResults;
    }

    private Map<String, CommitResult> getResultsMap(Collection<String> commitHashes) {
        // if any of the hashes in commitHashes have no saved results, they will be omitted in the output
        Collection<CommitResult> results = resultAccess.getResultsFromCommits(commitHashes);
        Map<String, CommitResult> resultsMap = new HashMap<>();

        for (CommitResult result : results) {
            resultsMap.put(result.getCommitHash(), result);
        }

        return resultsMap;
    }

    private List<CommitHistoryItem> resultsToHistoryItems(List<CommitResult> results) {
        List<CommitHistoryItem> history = new LinkedList<>();

        for (CommitResult result : results) {
            ICommit commit = commitAccess.getCommit(result.getCommitHash());
            CommitHistoryItem historyItem = new CommitHistoryItem(result, commit);
            history.add(historyItem);
        }

        return history;
    }
}
