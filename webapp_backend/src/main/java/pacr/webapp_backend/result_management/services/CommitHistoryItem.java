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
    private final String commitHash;
    private final String commitMessage;
    private final String commitDate;
    private final String authorDate;
    private final String entryDate;

    private final boolean compared;
    private final boolean significant;

    private final boolean globalError;
    private final String globalErrorMessage;

    /**
     * Creates a new history item from a result and its commit.
     * @param result the result. Cannot be null.
     * @param commit the commit. Cannot be null.
     */
    public CommitHistoryItem(@NotNull final CommitResult result, @NotNull final ICommit commit) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(commit);

        this.commitHash = commit.getCommitHash();
        this.commitMessage = commit.getCommitMessage();
        this.commitDate = commit.getCommitDate().toString();
        this.authorDate = commit.getAuthorDate().toString();
        this.entryDate = result.getEntryDate().toString();

        this.compared = result.isCompared();
        this.significant = result.isSignificant();

        this.globalError = result.hasGlobalError();
        this.globalErrorMessage = result.getGlobalError();
    }
}
