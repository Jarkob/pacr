package pacr.webapp_backend.result_management.services;

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
     * @return the benchmarking result.
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
            throw new NoSuchElementException("no result for commit with hash " + commitHash + " was found");
        }

        return outputBuilder.buildDetailOutput(commit, result);
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
     * TODO test this?
     * Gets all benchmarking results for a branch with measurements for a specific benchmark.
     * @param benchmarkId the id of the benchmark.
     * @param repositoryId the id of the repository.
     * @param branch the name of the branch.
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
     * @return Gets up to 100 of the newest saved results.
     */
    public List<OutputBenchmarkingResult> getNewestResults() {
        List<CommitResult> results = resultAccess.getNewestResults();

        return resultsToOutputResults(results);
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
        Map<String, ICommit> hashToCommit = getHashToCommitMap(commits);

        Collection<CommitResult> results = getResultsAndRemoveOtherBenchmarks(hashToCommit.keySet(), benchmarkIdToKeep);

        HashMap<String, DiagramOutputResult> outputResults = new HashMap<>();

        for (CommitResult result : results) {
            ICommit commitForResult = hashToCommit.get(result.getCommitHash());
            DiagramOutputResult outputResult = outputBuilder.buildDiagramOutput(commitForResult, result);
            outputResults.put(result.getCommitHash(), outputResult);
        }

        return outputResults;
    }

    private Collection<CommitResult> getResultsAndRemoveOtherBenchmarks(Collection<String> commitHashes,
                                                                        int benchmarkIdToKeep) {
        // if any of the hashes in commitHashes have no saved results, they will be omitted in the output
        Collection<CommitResult> results = resultAccess.getResultsFromCommits(commitHashes);

        // remove unwanted benchmarks if benchmarkIdToKeep is not -1
        if (benchmarkIdToKeep != KEEP_ALL_BENCHMARK_DATA) {
            for (CommitResult result : results) {
                List<CommitResult> resultsWithoutOtherBenchmarks = new LinkedList<>();

                for (BenchmarkResult benchmarkResult : result.getBenchmarksIterable()) {
                    if (benchmarkResult.getBenchmark().getId() == benchmarkIdToKeep) {
                        resultsWithoutOtherBenchmarks.add(result);
                    } else {
                        // don't worry
                        // this does not alter the result in the database
                        result.removeBenchmarkResult(benchmarkResult);
                    }
                }

                results = resultsWithoutOtherBenchmarks;
            }
        }

        return results;
    }

    private Map<String, ICommit> getHashToCommitMap(Collection<? extends ICommit> commits) {
        Map<String, ICommit> hashToCommit = new HashMap<>();

        for (ICommit commit : commits) {
            hashToCommit.put(commit.getCommitHash(), commit);
        }

        return hashToCommit;
    }

    private List<OutputBenchmarkingResult> resultsToOutputResults(List<CommitResult> results) {
        List<OutputBenchmarkingResult> outputResultsSameOrder = new LinkedList<>();

        for (CommitResult result : results) {
            ICommit commit = commitAccess.getCommit(result.getCommitHash());
            OutputBenchmarkingResult outputResult = outputBuilder.buildDetailOutput(commit, result);
            outputResultsSameOrder.add(outputResult);
        }

        return outputResultsSameOrder;
    }
}
