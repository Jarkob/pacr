package pacr.webapp_backend.result_management.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a benchmarking result for a certain commit. Contains data about the commit and all measurements and/or
 * error messages.
 */
public class OutputBenchmarkingResult implements IBenchmarkingResult {

    private boolean hasGlobalError;
    private String errorMessage;

    private String commitHash;
    private String commitMessage;
    private String commitURL;
    private String comparisonCommitHash;

    /**
     * The dates are saved as strings in order to be readable in the json that is sent to the front end.
     */
    private String commitEntryDate;
    private String commitCommitDate;
    private String commitAuthorDate;

    private int commitRepositoryId;
    private String commitRepositoryName;
    private Collection<String> commitBranchNames;
    private Collection<String> commitParentHashes;
    private Collection<String> commitLabels;

    private ISystemEnvironment systemEnvironment;
    private OutputBenchmark[] benchmarksList;

    /**
     * Creates an OutputBenchmarkingResult for a commit. Copies system environment and error information from the
     * CommitResult and copies commit meta data from the ICommit. Throws IllegalArgumentException if the CommitResult
     * refers to a different commit hash than the ICommit or if one of the parameters is null.
     * @param commit the commit.
     * @param result the result for the commit.
     * @param benchmarks the benchmarks, their properties and their corresponding measurements.
     */
    OutputBenchmarkingResult(@NotNull ICommit commit, @NotNull CommitResult result,
                             @NotNull OutputBenchmark[] benchmarks) {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(result);
        Objects.requireNonNull(benchmarks);

        if (!belongToSameCommit(commit, result)) {
            throw new IllegalArgumentException("commit and result must belong to same commit hash");
        }
        this.hasGlobalError = result.hasGlobalError();
        this.errorMessage = result.getGlobalError();
        this.commitHash = commit.getCommitHash();
        this.commitURL = commit.getCommitURL();
        this.commitMessage = commit.getCommitMessage();
        this.comparisonCommitHash = result.getComparisonCommitHash();

        this.commitEntryDate = commit.getEntryDate().toString();
        this.commitCommitDate = commit.getCommitDate().toString();
        this.commitAuthorDate = commit.getAuthorDate().toString();

        this.commitRepositoryId = commit.getRepositoryID();
        this.commitRepositoryName = commit.getRepositoryName();

        this.commitBranchNames = commit.getBranchNames();

        this.commitParentHashes = commit.getParentHashes();

        this.commitLabels = commit.getLabels();

        this.systemEnvironment = result.getSystemEnvironment();
        this.benchmarksList = benchmarks;
    }

    @Override
    public int getRepositoryID() {
        return commitRepositoryId;
    }

    @Override
    public String getCommitHash() {
        return commitHash;
    }

    @Override
    public ISystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
    }

    @Override @JsonIgnore
    public Map<String, IBenchmark> getBenchmarks() {
        Map<String, IBenchmark> benchmarkMap = new HashMap<>();

        for (OutputBenchmark benchmark : benchmarksList) {
            benchmarkMap.put(benchmark.getOriginalName(), benchmark);
        }

        return benchmarkMap;
    }

    @Override @JsonIgnore
    public String getGlobalError() {
        if (getHasGlobalError()) {
            return errorMessage;
        }
        return null;
    }

    /**
     * Gets all benchmarks (for output)that were executed on the commit.
     * @return the benchmarks.
     */
    public List<OutputBenchmark> getBenchmarksList() {
        return Arrays.asList(benchmarksList);
    }

    /**
     * Indicates whether there was global error while benchmarking the commit.
     * @return true if there was a global error. Otherwise false.
     */
    public boolean getHasGlobalError() {
        return hasGlobalError;
    }

    private boolean belongToSameCommit(ICommit commit, CommitResult result) {
        if (commit != null && result != null) {
            return commit.getCommitHash().equals(result.getCommitHash());
        }
        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public String getCommitURL() {
        return commitURL;
    }

    public String getComparisonCommitHash() {
        return comparisonCommitHash;
    }

    public String getCommitEntryDate() {
        return commitEntryDate;
    }

    public String getCommitCommitDate() {
        return commitCommitDate;
    }

    public String getCommitAuthorDate() {
        return commitAuthorDate;
    }

    public int getCommitRepositoryId() {
        return commitRepositoryId;
    }

    public String getCommitRepositoryName() {
        return commitRepositoryName;
    }

    public Collection<String> getCommitBranchNames() {
        return commitBranchNames;
    }

    public Collection<String> getCommitParentHashes() {
        return commitParentHashes;
    }

    public Collection<String> getCommitLabels() {
        return commitLabels;
    }
}
