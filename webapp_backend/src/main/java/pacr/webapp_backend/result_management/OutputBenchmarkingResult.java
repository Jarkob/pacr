package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ISystemEnvironment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
    private SystemEnvironment systemEnvironment;
    private OutputBenchmarkGroup[] groups;

    /**
     * Creates a benchmarking result for a commit that was successfully benchmarked without a global error.
     * @param commit the commit.
     * @param systemEnvironment the system environment the commit was benchmarked on.
     * @param groups the benchmark groups with benchmarks, their properties and their corresponding measurements.
     */
    OutputBenchmarkingResult(ICommit commit, SystemEnvironment systemEnvironment, OutputBenchmarkGroup[] groups) {
        this.hadGlobalError = false;
        this.errorMessage = null;
        this.commit = commit;
        this.systemEnvironment = systemEnvironment;
        this.groups = groups;
    }

    /**
     * Creates a benchmarking result for a commit with a global error. no benchmark groups with measurements are saved
     * for this result.
     * @param commit the commit.
     * @param systemEnvironment the system environment the commit was (attempted to be) benchmarked on.
     * @param errorMessage the global error message.
     */
    OutputBenchmarkingResult(ICommit commit, SystemEnvironment systemEnvironment, String errorMessage) {
        this.hadGlobalError = true;
        this.errorMessage = errorMessage;
        this.commit = commit;
        this.systemEnvironment = systemEnvironment;
    }

    @Override
    public String getRepository() {
        return commit.getRepositoryURL();
    }

    @Override
    public String getCommitHash() {
        return commit.getHash();
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
    public Date getAuthorDate() {
        return commit.getAuthorDate();
    }

    /**
     * Gets the commit date of the benchmarked commit.
     * @return the commit date.
     */
    public Date getCommitDate() {
        return commit.getCommitDate();
    }

    /**
     * Gets the entry date of the benchmarked commit (when it was entered into this system).
     * @return the entry date.
     */
    public Date getEntryDate() {
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
        return commit.getBranch();
    }

    /**
     * Gets the label of the benchmarked commit.
     * @return the label.
     */
    public String getLabel() {
        return commit.getLabel();
    }

    /**
     * Gets all commit hashes of parents of the benchmarked commit.
     * @return the commit hashes.
     */
    public Collection<String> getParentHashes() {
        List<String> parentHashes = new LinkedList<>();
        for (ICommit commit : commit.getParents()) {
            parentHashes.add(commit.getHash());
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
