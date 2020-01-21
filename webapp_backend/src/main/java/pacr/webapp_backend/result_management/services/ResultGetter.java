package pacr.webapp_backend.result_management.services;

import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
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
import java.util.Objects;
import java.util.Set;

/**
 * Gets benchmarking results from repository, branches or single commits from the database and updates observers about
 * newly saved results (that weren't imported from outside the system).
 */
@Component
public class ResultGetter implements ICommitBenchmarkedChecker, INewestResult, IResultExporter {

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
    public Collection<OutputBenchmarkingResult> getRepositoryResults(int repositoryId) {
        Collection<? extends ICommit> commits = commitAccess.getCommitsFromRepository(repositoryId);
        return commitsToOutputResults(commits);
    }

    /**
     * Gets all saved benchmarking results of a branch.
     * @param repositoryId the id of the repository of the branch.
     * @param branch the name of the branch. Cannot be null, empty or blank.
     * @return the benchmarking results.
     * @throws NotFoundException if no repository with the id or no branch with the name could be found.
     */
    public Collection<OutputBenchmarkingResult> getBranchResults(int repositoryId, @NotNull String branch)
            throws NotFoundException {
        if (!StringUtils.hasText(branch)) {
            throw new IllegalArgumentException("branch cannot be null, empty or blank");
        }

        Collection<? extends ICommit> commits = commitAccess.getCommitsFromBranch(repositoryId, branch);

        if (commits == null) {
            throw new NotFoundException("no repository with id " + repositoryId + " or no branch with name "
                    + branch + " was found");
        }

        return commitsToOutputResults(commits);
    }

    /**
     * Gets the benchmarking result of a commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @return the benchmarking result.
     * @throws NotFoundException if no commit with this hash or no result for this commit could be found.
     */
    public OutputBenchmarkingResult getCommitResult(@NotNull String commitHash) throws NotFoundException {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        ICommit commit = commitAccess.getCommit(commitHash);

        if (commit == null) {
            throw new NotFoundException("no commit with hash " + commitHash + " was found.");
        }

        CommitResult result = resultAccess.getResultFromCommit(commitHash);

        if (result == null) {
            throw new NotFoundException("no result for commit with hash " + commitHash + " was found");
        }

        return outputBuilder.buildOutput(commit, result);
    }

    /**
     * Gets all benchmarking results with a specific measurements for a specific benchmark.
     * @param benchmarkId the id of the benchmark.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is not being
     *         omitted)
     */
    public Collection<OutputBenchmarkingResult> getBenchmarkResults(int benchmarkId) {
        // TODO currently incredibly inefficient

        List<CommitResult> allResults = this.resultAccess.getAllResults();
        List<CommitResult> resultsWithoutOtherBenchmarks = new LinkedList<>();

        for (CommitResult result : allResults) {
            for (BenchmarkResult benchmarkResult : result.getBenchmarksIterable()) {
                if (benchmarkResult.getBenchmark().getId() == benchmarkId) {
                    resultsWithoutOtherBenchmarks.add(result);
                } else {
                    // don't worry
                    // this does not alter the result in the database
                    result.removeBenchmarkResult(benchmarkResult);
                }
            }
        }

        return resultsToOutputResults(resultsWithoutOtherBenchmarks);
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

    private List<OutputBenchmarkingResult> commitsToOutputResults(Collection<? extends ICommit> commits) {
        Collection<String> commitHashes = new LinkedList<>();
        Map<String, ICommit> hashToCommit = new HashMap<>();

        for (ICommit commit : commits) {
            commitHashes.add(commit.getCommitHash());
            hashToCommit.put(commit.getCommitHash(), commit);
        }

        // if any of the hashes in commitHashes have no saved results, they will be omitted in the output
        Collection<CommitResult> results = resultAccess.getResultsFromCommits(commitHashes);
        List<OutputBenchmarkingResult> outputResults = new LinkedList<>();

        for (CommitResult result : results) {
            ICommit commitForResult = hashToCommit.get(result.getCommitHash());
            OutputBenchmarkingResult outputResult = outputBuilder.buildOutput(commitForResult, result);
            outputResults.add(outputResult);
        }

        return outputResults;
    }

    private List<OutputBenchmarkingResult> resultsToOutputResults(List<CommitResult> results) {
        List<OutputBenchmarkingResult> outputResultsSameOrder = new LinkedList<>();

        for (CommitResult result : results) {
            ICommit commit = commitAccess.getCommit(result.getCommitHash());
            OutputBenchmarkingResult outputResult = outputBuilder.buildOutput(commit, result);
            outputResultsSameOrder.add(outputResult);
        }

        return outputResultsSameOrder;
    }
}
