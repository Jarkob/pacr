package pacr.webapp_backend.result_management.services;

import lombok.Getter;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a benchmarking result for a certain commit. Contains data about the commit and all measurements and/or
 * error messages. @Getter provides this class with all getters so the json can be properly created.
 */
@Getter
public class OutputBenchmarkingResult {

    private boolean globalError;
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
        this.globalError = result.hasGlobalError();
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

        this.globalError = false;
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
     * Gets all benchmarks (for output) that were executed on the commit.
     * @return the benchmarks.
     */
    public List<OutputBenchmark> getBenchmarksList() {
        return Arrays.asList(benchmarksList);
    }

    private boolean belongToSameCommit(ICommit commit, CommitResult result) {
        if (commit != null && result != null) {
            return commit.getCommitHash().equals(result.getCommitHash());
        }
        return false;
    }
}
