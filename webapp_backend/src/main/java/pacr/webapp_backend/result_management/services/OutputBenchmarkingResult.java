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

    private final boolean globalError;
    private final String errorMessage;

    private final String commitHash;
    private final String commitMessage;
    private final String commitURL;
    private String comparisonCommitHash;

    /**
     * The dates are saved as strings in order to be readable in the json that is sent to the front end.
     */
    private final String commitEntryDate;
    private final String commitCommitDate;
    private final String commitAuthorDate;

    private final int commitRepositoryId;
    private final String commitRepositoryName;
    private final Collection<String> commitBranchNames;
    private final Collection<String> commitParentHashes;
    private final Collection<String> commitLabels;

    private ISystemEnvironment systemEnvironment;
    private final OutputBenchmark[] benchmarksList;

    /**
     * Creates an OutputBenchmarkingResult for a commit. Copies system environment and error information from the
     * CommitResult and copies commit meta data from the ICommit. Throws IllegalArgumentException if the CommitResult
     * refers to a different commit hash than the ICommit or if one of the parameters is null.
     * @param commit the commit.
     * @param result the result for the commit.
     * @param benchmarks the benchmarks, their properties and their corresponding measurements.
     */
    OutputBenchmarkingResult(@NotNull final ICommit commit, @NotNull final CommitResult result,
                             @NotNull final OutputBenchmark... benchmarks) {
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
        if (result.isCompared()) {
            this.comparisonCommitHash = result.getComparisonCommitHash();
        }

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
    OutputBenchmarkingResult(@NotNull final ICommit commit) {
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

    private boolean belongToSameCommit(ICommit commit, CommitResult result) {
        if (commit != null && result != null) {
            return commit.getCommitHash().equals(result.getCommitHash());
        }
        return false;
    }
}
