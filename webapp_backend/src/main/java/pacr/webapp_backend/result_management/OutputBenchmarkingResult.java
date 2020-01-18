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

/**
 * Represents a benchmarking result for a certain commit. Contains data about the commit and all measurements and/or
 * error messages.
 */
public class OutputBenchmarkingResult implements IBenchmarkingResult {

    private boolean globalError;
    private String errorMessage;

    private String commitHash;
    private String commitMessage;

    /**
     * The dates are saved as strings in order to be readable in the json that is sent to the front end.
     */
    private String commitEntryDate;
    private String commitCommitDate;
    private String commitAuthorDate;

    private int commitRepositoryId;
    private String commitBranchName;
    private Collection<String> commitParentHashes;
    private Collection<String> commitLabels;

    private ISystemEnvironment systemEnvironment;
    private OutputBenchmarkGroup[] groups;

    /**
     * Creates an OutputBenchmarkingResult for a commit. Copies system environment and error information from the
     * CommitResult and copies commit meta data from the ICommit. Throws IllegalArgumentException if the CommitResult
     * refers to a different commit hash than the ICommit or if one of the parameters is null.
     * @param commit the commit.
     * @param result the result for the commit.
     * @param groups the benchmark groups with benchmarks, their properties and their corresponding measurements.
     */
    OutputBenchmarkingResult(@NotNull ICommit commit, @NotNull CommitResult result,
                             @NotNull OutputBenchmarkGroup[] groups) {
        if (commit == null || result == null || groups == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        if (!belongToSameCommit(commit, result)) {
            throw new IllegalArgumentException("commit and result must belong to same commit hash");
        }
        this.globalError = result.hasGlobalError();
        this.errorMessage = result.getGlobalError();
        this.commitHash = commit.getCommitHash();
        this.commitMessage = commit.getMessage();

        this.commitEntryDate = commit.getEntryDate().toString();
        this.commitCommitDate = commit.getCommitDate().toString();
        this.commitAuthorDate = commit.getAuthorDate().toString();

        this.commitRepositoryId = commit.getRepositoryID();
        this.commitBranchName = commit.getBranchName();

        List<String> parentHashes = new LinkedList<>();
        for (ICommit parent : commit.getParents()) {
            parentHashes.add(parent.getCommitHash());
        }
        this.commitParentHashes = parentHashes;

        this.commitLabels = commit.getLabels();

        this.systemEnvironment = result.getSystemEnvironment();
        this.groups = groups;
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
        Map<String, IBenchmark> benchmarks = new HashMap<>();

        for (OutputBenchmarkGroup group : groups) {
            for (OutputBenchmark benchmark : group.getBenchmarks()) {
                benchmarks.put(benchmark.getOriginalName(), benchmark);
            }
        }

        return benchmarks;
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
    public String getBranch() {
        return commitBranchName;
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
     * Gets all benchmark groups (for output) with benchmarks that were executed on the commit.
     * @return the groups.
     */
    public List<OutputBenchmarkGroup> getBenchmarkGroups() {
        return Arrays.asList(groups);
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
