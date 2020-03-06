package pacr.webapp_backend.result_management.services;

import java.time.LocalDateTime;
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

    private final IGetCommitAccess commitAccess;
    private final IResultAccess resultAccess;
    private final OutputBuilder outputBuilder;

    private final Set<IObserver> observers;

    /**
     * Creates a new ResultGetter. Dependencies are injected.
     * @param commitAccess access to commits.
     * @param resultAccess access to results.
     * @param outputBuilder builder for output.
     */
    public ResultGetter(final IGetCommitAccess commitAccess, final IResultAccess resultAccess, final OutputBuilder outputBuilder) {
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
    public OutputBenchmarkingResult getCommitResult(@NotNull final String commitHash) {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        final ICommit commit = commitAccess.getCommit(commitHash);

        if (commit == null) {
            throw new NoSuchElementException("no commit with hash " + commitHash + " was found.");
        }

        final CommitResult result = resultAccess.getResultFromCommit(commitHash);

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
    public Page<OutputBenchmarkingResult> getFullRepositoryResults(final int repositoryId, final Pageable pageable) {
        Objects.requireNonNull(pageable);

        final Page<CommitResult> results = resultAccess.getFullRepositoryResults(repositoryId, pageable);

        final List<OutputBenchmarkingResult> outputBenchmarkingResults = new LinkedList<>();

        for (final CommitResult result : results) {
            final ICommit commit = commitAccess.getCommit(result.getCommitHash());
            if (commit != null) {
                final OutputBenchmarkingResult outputResult = outputBuilder.buildDetailOutput(commit, result);
                outputBenchmarkingResults.add(outputResult);
            }
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
    public Map<String, DiagramOutputResult> getBenchmarkResults(final int repositoryId, final int benchmarkId) {
        final Collection<? extends ICommit> commitsFromRepository = commitAccess.getCommitsFromRepository(repositoryId);
        return commitsToDiagramResults(commitsFromRepository, benchmarkId);
    }

    /**
     * Gets a subset of the benchmarking results for a branch with measurements for a specific benchmark.
     * @param benchmarkId the id of the benchmark.
     * @param repositoryId the id of the repository.
     * @param branch the name of the branch. Cannot be null.
     * @param commitDateStart the start date of the requested commits. Cannot be null.
     * @param commitDateEnd the end date of the requested commits. Cannot be null.
     * @return the benchmarking results (containing only the requested benchmark, all other benchmark data is being
     *         omitted).
     */
    public Map<String, DiagramOutputResult> getBenchmarkResultsSubset(int benchmarkId, int repositoryId,
                                                                          @NotNull String branch,
                                                                          @NotNull LocalDateTime commitDateStart,
                                                                          @NotNull LocalDateTime commitDateEnd) {
        Objects.requireNonNull(branch);
        Objects.requireNonNull(commitDateStart);
        Objects.requireNonNull(commitDateEnd);

        List<? extends ICommit> branchCommitsInTimeFrame =
                commitAccess.getCommitsFromBranchTimeFrame(repositoryId, branch, commitDateStart, commitDateEnd);

        if (branchCommitsInTimeFrame == null) {
            return new HashMap<>();
        }

        return commitsToDiagramResults(branchCommitsInTimeFrame, benchmarkId);
    }

    /**
     * Gets the newest saved results.
     * @param pageable the requested page.
     * @return the results.
     */
    public Page<CommitHistoryItem> getNewestResults(final Pageable pageable) {
        final Page<CommitResult> resultsPage = resultAccess.getNewestResults(pageable);

        final List<CommitHistoryItem> historyItems = resultsToHistoryItems(resultsPage.getContent());

        return new PageImpl<>(historyItems, pageable, resultsPage.getTotalElements());
    }

    private List<CommitHistoryItem> resultsToHistoryItems(final List<CommitResult> results) {
        final List<CommitHistoryItem> history = new LinkedList<>();

        for (CommitResult result : results) {
            ICommit commit = commitAccess.getCommit(result.getCommitHash());
            if (commit != null) {
                CommitHistoryItem historyItem = new CommitHistoryItem(result, commit);
                history.add(historyItem);
            }
        }

        return history;
    }

    /**
     * Gets all measurements that were made for a benchmark property for a commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @param benchmarkId the id of the benchmark of the property.
     * @param propertyName the name of the property. Cannot be null, empty or blank.
     * @return the measurements for this property and commit.
     */
    public List<Double> getMeasurementsOfPropertyForCommit(@NotNull final String commitHash,
                                                           final int benchmarkId,
                                                           @NotNull final String propertyName) {
        if (!StringUtils.hasText(commitHash) || !StringUtils.hasText(propertyName)) {
            throw new IllegalArgumentException("input cannot be null, empty or blank");
        }

        final CommitResult result = resultAccess.getResultFromCommit(commitHash);
        if (result == null) {
            LOGGER.error("no result found for commit {}", commitHash);
            return new LinkedList<>();
        }

        for (final BenchmarkResult benchmarkResult : result.getBenchmarkResults()) {

            if (benchmarkResult.getBenchmark().getId() == benchmarkId) {
                for (final BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                    if (propertyResult.getName().equals(propertyName)) {
                        return propertyResult.getResults();
                    }
                }
                break;
            }

        }

        LOGGER.error("property \"{}\" not found for commit {}", propertyName, commitHash);
        return new LinkedList<>();
    }

    @Override
    public IBenchmarkingResult getNewestResult(final int repositoryID) {
        return resultAccess.getNewestResult(repositoryID);
    }

    @Override
    public List<? extends IBenchmarkingResult> exportAllBenchmarkingResults() {
        return resultAccess.getAllResults();
    }

    @Override
    public boolean isCommitBenchmarked(@NotNull final String commitHash) {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        return resultAccess.getResultFromCommit(commitHash) != null;
    }

    @Override
    public void subscribe(@NotNull final IObserver observer) {
        Objects.requireNonNull(observer);
        observers.add(observer);
    }

    @Override
    public void unsubscribe(@NotNull final IObserver observer) {
        Objects.requireNonNull(observer);
        observers.remove(observer);
    }

    @Override
    public void updateAll() {
        for (final IObserver observer : observers) {
            observer.update();
        }
    }

    private Map<String, DiagramOutputResult> commitsToDiagramResults(final Collection<? extends ICommit> commits,
                                                                         final int benchmarkId) {
        final Collection<String> hashes = new LinkedList<>();
        for (final ICommit commit : commits) {
            hashes.add(commit.getCommitHash());
        }

        final Map<String, CommitResult> results = getResultsMap(hashes);

        final HashMap<String, DiagramOutputResult> outputResults = new HashMap<>();

        for (final ICommit commit : commits) {
            final CommitResult resultForCommit = results.get(commit.getCommitHash());

            final DiagramOutputResult outputResult;

            if (resultForCommit != null) {
                outputResult = outputBuilder.buildDiagramOutput(commit, resultForCommit, benchmarkId);
            } else {
                outputResult = outputBuilder.buildDiagramOutput(commit);
            }

            outputResults.put(commit.getCommitHash(), outputResult);
        }

        return outputResults;
    }

    private Map<String, CommitResult> getResultsMap(final Collection<String> commitHashes) {
        // if any of the hashes in commitHashes have no saved results, they will be omitted in the output
        final Collection<CommitResult> results = resultAccess.getResultsFromCommits(commitHashes);
        final Map<String, CommitResult> resultsMap = new HashMap<>();

        for (final CommitResult result : results) {
            resultsMap.put(result.getCommitHash(), result);
        }

        return resultsMap;
    }

}
