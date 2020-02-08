package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a benchmarking result for a certain commit. Contains data about the commit and all measurements and/or
 * error messages.
 */
public class OutputBenchmarkingResult {

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

    /**
     * Creates an OutputBenchmarkingResult for a commit without a result. Copies data from the commit and adds an
     * error message because no result data is present. The system environment and comparisonCommitHash remains null
     * and the benchmarksList is empty.
     * @param commit the commit. Cannot be null.
     */
    OutputBenchmarkingResult(@NotNull ICommit commit) {
        Objects.requireNonNull(commit);

        this.hasGlobalError = false;
        this.errorMessage = null;

        this.commitHash = commit.getCommitHash();
        this.commitURL = commit.getCommitURL();
        this.commitMessage = commit.getCommitMessage();

        this.commitEntryDate = commit.getEntryDate().toString();
        this.commitCommitDate = commit.getCommitDate().toString();
        this.commitAuthorDate = commit.getAuthorDate().toString();

        this.commitRepositoryId = commit.getRepositoryID();
        this.commitRepositoryName = commit.getRepositoryName();

        this.commitBranchNames = commit.getBranchNames();

        this.commitParentHashes = commit.getParentHashes();

        this.commitLabels = commit.getLabels();

        this.benchmarksList = new OutputBenchmark[0];
    }

    /**
     * @return the commit hash of the result.
     */
    public String getCommitHash() {
        return commitHash;
    }

    /**
     * @return the system environment the result was measured on.
     */
    public ISystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
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

    /**
     * @return the global error message of this result.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the message of the commit of this result.
     */
    public String getCommitMessage() {
        return commitMessage;
    }

    /**
     * @return the url of the commit of this result.
     */
    public String getCommitURL() {
        return commitURL;
    }

    /**
     * @return the hash of the commit this result was compared to. May be null if no comparison was done.
     */
    public String getComparisonCommitHash() {
        return comparisonCommitHash;
    }

    /**
     * @return the entry date of the commit of this result into the database.
     */
    public String getCommitEntryDate() {
        return commitEntryDate;
    }

    /**
     * @return the commit date of the commit of this result.
     */
    public String getCommitCommitDate() {
        return commitCommitDate;
    }

    /**
     * @return the author date of the commit of this result.
     */
    public String getCommitAuthorDate() {
        return commitAuthorDate;
    }

    /**
     * @return the id of the repository of the commit of this result.
     */
    public int getCommitRepositoryId() {
        return commitRepositoryId;
    }

    /**
     * @return the name of the repository of the commit of this result.
     */
    public String getCommitRepositoryName() {
        return commitRepositoryName;
    }

    /**
     * @return the names of the branches of the commit of this result.
     */
    public Collection<String> getCommitBranchNames() {
        return commitBranchNames;
    }

    /**
     * @return the hashes of the parents of the commit of this result.
     */
    public Collection<String> getCommitParentHashes() {
        return commitParentHashes;
    }

    /**
     * @return the labels of the commit of this result.
     */
    public Collection<String> getCommitLabels() {
        return commitLabels;
    }

    private boolean belongToSameCommit(ICommit commit, CommitResult result) {
        if (commit != null && result != null) {
            return commit.getCommitHash().equals(result.getCommitHash());
        }
        return false;
    }
}
