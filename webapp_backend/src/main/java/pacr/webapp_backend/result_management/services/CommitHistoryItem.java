package pacr.webapp_backend.result_management.services;

import lombok.Getter;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * An item of the commit history with the newest results.
 */
@Getter
public class CommitHistoryItem {
    private String commitHash;
    private String commitMessage;
    private String commitDate;
    private String authorDate;
    private String entryDate;

    private boolean compared;
    private boolean significant;

    private boolean globalError;
    private String globalErrorMessage;

    /**
     * Creates a new history item from a result and its commit.
     * @param result the result. Cannot be null.
     * @param commit the commit. Cannot be null.
     */
    public CommitHistoryItem(@NotNull CommitResult result, @NotNull ICommit commit) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(commit);

        this.commitHash = commit.getCommitHash();
        this.commitMessage = commit.getCommitMessage();
        this.commitDate = commit.getCommitDate().toString();
        this.authorDate = commit.getAuthorDate().toString();
        this.entryDate = result.getEntryDate().toString();

        this.compared = result.getComparisonCommitHash() != null;
        this.significant = result.isSignificant();

        this.globalError = result.hasGlobalError();
        this.globalErrorMessage = result.getGlobalError();
    }
}
