package pacr.webapp_backend.result_management.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ICommitBenchmarkedChecker;
import pacr.webapp_backend.shared.INewestResult;
import pacr.webapp_backend.shared.IObserver;
import pacr.webapp_backend.shared.IResultExporter;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * Gets benchmarking results from repository, branches or single commits from the database and updates observers about
 * newly saved results (that weren't imported from outside the system).
 */
@Component
public class ResultGetter implements ICommitBenchmarkedChecker, INewestResult, IResultExporter {

    private static final int KEEP_ALL_BENCHMARK_DATA = -1;

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
     * Gets all saved benchmarking results of a repository.
     * @param repositoryId the id of the repository.
     * @return the benchmarking results.
     */
    public HashMap<String, DiagramOutputResult> getRepositoryResults(int repositoryId) {
        Collection<? extends ICommit> commits = commitAccess.getCommitsFromRepository(repositoryId);
        return commitsToDiagramResults(commits, KEEP_ALL_BENCHMARK_DATA);
    }

    /**
     * Gets all saved benchmarking results of a branch.
     * @param repositoryId the id of the repository of the branch.
     * @param branch the name of the branch. Cannot be null, empty or blank.
     * @return the benchmarking results.
     */
    public HashMap<String, DiagramOutputResult> getBranchResults(int repositoryId, @NotNull String branch) {
        if (!StringUtils.hasText(branch)) {
            throw new IllegalArgumentException("branch cannot be null, empty or blank");
        }

        Collection<? extends ICommit> commits = commitAccess.getCommitsFromBranch(repositoryId, branch);

        if (commits == null) {
            throw new NoSuchElementException("no repository with id " + repositoryId + " or no branch with name "
                    + branch + " was found");
        }

        return commitsToDiagramResults(commits, KEEP_ALL_BENCHMARK_DATA);
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
     * Gets all benchmarking results with a specific measurements for a specific benchmark.
     * @param benchmarkId the id of the benchmark.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is being
     *         omitted)
     */
    public HashMap<String, DiagramOutputResult> getBenchmarkResults(int benchmarkId) {
        // TODO currently incredibly inefficient

        Collection<? extends ICommit> commits = this.commitAccess.getAllCommits();
        return commitsToDiagramResults(commits, benchmarkId);
    }

    /**
     * Gets all benchmarking results for a repository with measurements for a specific benchmark.
     * @param repositoryId the id of the repository.
     * @param benchmarkId the id of the benchmark.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is not being
     *         omitted)
     */
    public HashMap<String, DiagramOutputResult> getBenchmarkResults(int repositoryId, int benchmarkId) {
        Collection<? extends ICommit> commitsFromRepository = commitAccess.getCommitsFromRepository(repositoryId);
        return commitsToDiagramResults(commitsFromRepository, benchmarkId);
    }

    /**
     * Gets all benchmarking results for a branch with measurements for a specific benchmark.
     * @param benchmarkId the id of the benchmark.
     * @param repositoryId the id of the repository.
     * @param branch the name of the branch. Cannot be null.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is not being
     *         omitted)
     */
    public HashMap<String, DiagramOutputResult> getBenchmarkResults(int benchmarkId, int repositoryId,
                                                                    @NotNull String branch) {
        Objects.requireNonNull(branch);
        Collection<? extends ICommit> commitsFromBranch = commitAccess.getCommitsFromBranch(repositoryId, branch);
        return commitsToDiagramResults(commitsFromBranch, benchmarkId);
    }

    /**
     * Gets a subset of the benchmarking results for a branch with measurements for a specific benchmark.
     * @param benchmarkId the id of the benchmark.
     * @param repositoryId the id of the repository.
     * @param branch the name of the branch. Cannot be null.
     * @param page the number of the requested page.
     * @param size the size of the page.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is not being
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
     * @return Gets up to 100 of the newest saved results.
     */
    public List<CommitHistoryItem> getNewestResults() {
        List<CommitResult> results = resultAccess.getNewestResults();

        return resultsToHistoryItems(results);
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
                                                              int benchmarkIdToKeep) {
        Collection<String> hashes = new LinkedList<>();
        for (ICommit commit : commits) {
            hashes.add(commit.getCommitHash());
        }

        Map<String, CommitResult> results = getResultsAndRemoveOtherBenchmarks(hashes, benchmarkIdToKeep);

        HashMap<String, DiagramOutputResult> outputResults = new HashMap<>();

        for (ICommit commit : commits) {
            CommitResult resultForCommit = results.get(commit.getCommitHash());

            DiagramOutputResult outputResult = null;

            if (resultForCommit != null) {
                outputResult = outputBuilder.buildDiagramOutput(commit, resultForCommit);
            } else {
                outputResult = outputBuilder.buildDiagramOutput(commit);
            }

            outputResults.put(commit.getCommitHash(), outputResult);
        }

        return outputResults;
    }

    private Map<String, CommitResult> getResultsAndRemoveOtherBenchmarks(Collection<String> commitHashes,
                                                                        int benchmarkIdToKeep) {
        // if any of the hashes in commitHashes have no saved results, they will be omitted in the output
        Collection<CommitResult> results = resultAccess.getResultsFromCommits(commitHashes);
        Map<String, CommitResult> resultsMap = new HashMap<>();

        for (CommitResult result : results) {
            if (benchmarkIdToKeep == KEEP_ALL_BENCHMARK_DATA) {
                resultsMap.put(result.getCommitHash(), result);
            } else {
                // remove unwanted benchmarks if benchmarkIdToKeep is not -1
                List<BenchmarkResult> benchmarksToRemove = new LinkedList<>();

                for (BenchmarkResult benchmarkResult : result.getBenchmarkResults()) {
                    if (benchmarkResult.getBenchmark().getId() == benchmarkIdToKeep) {
                        resultsMap.put(result.getCommitHash(), result);
                    } else {
                        benchmarksToRemove.add(benchmarkResult);
                    }
                }

                for (BenchmarkResult benchmarkToRemove : benchmarksToRemove) {
                    // don't worry
                    // this does not alter the result in the database
                    result.removeBenchmarkResult(benchmarkToRemove);
                }
            }
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
