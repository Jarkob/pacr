package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a benchmarking result for a certain commit. Contains data about the commit and all measurements and/or
 * error messages.
 */
public class OutputBenchmarkingResult implements IBenchmarkingResult {

    private boolean globalError;
    private String errorMessage;

    private String commitHash;
    private String commitMessage;
    private String comparisonCommitHash;

    /**
     * The dates are saved as strings in order to be readable in the json that is sent to the front end.
     */
    private String commitEntryDate;
    private String commitCommitDate;
    private String commitAuthorDate;

    private int commitRepositoryId;
    private Collection<String> commitBranchNames;
    private Collection<String> commitParentHashes;
    private Collection<String> commitLabels;

    private ISystemEnvironment systemEnvironment;
    private OutputBenchmark[] benchmarks;

    /**
     * Creates an OutputBenchmarkingResult for a commit. Copies system environment and error information from the
     * CommitResult and copies commit meta data from the ICommit. Throws IllegalArgumentException if the CommitResult
     * refers to a different commit hash than the ICommit or if one of the parameters is null.
     * @param commit the commit.
     * @param result the result for the commit.
     * @param benchmarks the benchmarks, their properties and their corresponding measurements.
     */
    public OutputBenchmarkingResult(@NotNull ICommit commit, @NotNull CommitResult result,
                             @NotNull OutputBenchmark[] benchmarks) {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(result);
        Objects.requireNonNull(benchmarks);

        if (!belongToSameCommit(commit, result)) {
            throw new IllegalArgumentException("commit and result must belong to same commit hash");
        }
        this.globalError = result.hasGlobalError();
        this.errorMessage = result.getGlobalError();
        this.commitHash = commit.getCommitHash();
        this.commitMessage = commit.getMessage();
        this.comparisonCommitHash = result.getComparisonCommitHash();

        this.commitEntryDate = commit.getEntryDate().toString();
        this.commitCommitDate = commit.getCommitDate().toString();
        this.commitAuthorDate = commit.getAuthorDate().toString();

        this.commitRepositoryId = commit.getRepositoryID();

        // TODO change this as soon as ICommit interface is up to date
        this.commitBranchNames = new LinkedList<>();

        this.commitParentHashes = commit.getParentHashes();

        this.commitLabels = commit.getLabels();

        this.systemEnvironment = result.getSystemEnvironment();
        this.benchmarks = benchmarks;
    }

    @Override
    public String getCommitHash() {
        return commitHash;
    }

    @Override
    public ISystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
    }

    @Override
    public Map<String, IBenchmark> getBenchmarks() {
        Map<String, IBenchmark> benchmarkMap = new HashMap<>();

        for (OutputBenchmark benchmark : benchmarks) {
            benchmarkMap.put(benchmark.getOriginalName(), benchmark);
        }

        return benchmarkMap;
    }

    @Override
    public String getGlobalError() {
        if (hasGlobalError()) {
            return errorMessage;
        }
        return null;
    }

    /**
     * Gets the commit message of the benchmarked commit.
     * @return the commit message.
     */
    public String getMessage() {
        return commitMessage;
    }

    /**
     * Gets the author date of the benchmarked commit.
     * @return the author date.
     */
    public String getAuthorDate() {
        return commitAuthorDate;
    }

    /**
     * Gets the commit date of the benchmarked commit.
     * @return the commit date.
     */
    public String getCommitDate() {
        return commitCommitDate;
    }

    /**
     * Gets the entry date of the benchmarked commit (when it was entered into this system).
     * @return the entry date.
     */
    public String getEntryDate() {
        return commitEntryDate;
    }

    /**
     * Gets the id of the repository of the benchmarked commit.
     * @return the repository id.
     */
    public int getRepositoryID() {
        return commitRepositoryId;
    }

    /**
     * Gets the branch name of the benchmarked commit.
     * @return the branch name.
     */
    public Collection<String> getBranchNames() {
        return commitBranchNames;
    }

    /**
     * Gets the label of the benchmarked commit.
     * @return the label.
     */
    public Collection<String> getLabels() {
        return commitLabels;
    }

    /**
     * Gets all commit hashes of parents of the benchmarked commit.
     * @return the commit hashes.
     */
    public Collection<String> getParentHashes() {
        return commitParentHashes;
    }

    /**
     * Gets all benchmarks (for output)that were executed on the commit.
     * @return the benchmarks.
     */
    public List<OutputBenchmark> getBenchmarksList() {
        return Arrays.asList(benchmarks);
    }

    /**
     * Indicates whether there was global error while benchmarking the commit.
     * @return true if there was a global error. Otherwise false.
     */
    public boolean hasGlobalError() {
        return globalError;
    }

    private boolean belongToSameCommit(ICommit commit, CommitResult result) {
        if (commit != null && result != null) {
            return commit.getCommitHash().equals(result.getCommitHash());
        }
        return false;
    }
}
