package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ISystemEnvironment;

import java.time.LocalDate;
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

    private boolean hadGlobalError;
    private String errorMessage;
    private ICommit commit;
    private ISystemEnvironment systemEnvironment;
    private OutputBenchmarkGroup[] groups;

    /**
     * Creates an OutputBenchmarkingResult for a commit. Copies system environment and error information from the
     * CommitResult. Throws IllegalArgumentException if the CommitResult refers to a different commit hash than
     * the ICommit.
     * @param commit the commit.
     * @param result the result for the commit.
     * @param groups the benchmark groups with benchmarks, their properties and their corresponding measurements.
     */
    OutputBenchmarkingResult(ICommit commit, CommitResult result, OutputBenchmarkGroup[] groups) {
        if (!commit.getCommitHash().equals(result.getCommitHash())) {
            throw new IllegalArgumentException();
        }
        this.hadGlobalError = result.isError();
        this.errorMessage = result.getGlobalError();
        this.commit = commit;
        this.systemEnvironment = result.getSystemEnvironment();
        this.groups = groups;
    }

    @Override
    public String getCommitHash() {
        return commit.getCommitHash();
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
        return errorMessage;
    }

    /**
     * Gets the commit message of the benchmarked commit.
     * @return the commit message.
     */
    public String getMessage() {
        return commit.getMessage();
    }

    /**
     * Gets the author date of the benchmarked commit.
     * @return the author date.
     */
    public LocalDate getAuthorDate() {
        return commit.getAuthorDate();
    }

    /**
     * Gets the commit date of the benchmarked commit.
     * @return the commit date.
     */
    public LocalDate getCommitDate() {
        return commit.getCommitDate();
    }

    /**
     * Gets the entry date of the benchmarked commit (when it was entered into this system).
     * @return the entry date.
     */
    public LocalDate getEntryDate() {
        return commit.getEntryDate();
    }

    /**
     * Gets the id of the repository of the benchmarked commit.
     * @return the repository id.
     */
    public int getRepositoryID() {
        return commit.getRepositoryID();
    }

    /**
     * Gets the branch name of the benchmarked commit.
     * @return the branch name.
     */
    public String getBranch() {
        return commit.getBranchName();
    }

    /**
     * Gets the label of the benchmarked commit.
     * @return the label.
     */
    public Collection<String> getLabels() {
        return commit.getLabels();
    }

    /**
     * Gets all commit hashes of parents of the benchmarked commit.
     * @return the commit hashes.
     */
    public Collection<String> getParentHashes() {
        List<String> parentHashes = new LinkedList<>();
        for (ICommit commit : commit.getParents()) {
            parentHashes.add(commit.getCommitHash());
        }
        return parentHashes;
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
    public boolean hadGlobalError() {
        return hadGlobalError;
    }
}
