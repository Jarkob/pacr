package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Retrieves ICommits from the database. Cannot alter the database.
 */
public interface IGetCommitAccess {

    /**
     * Gets all saved commits from a repository. Returns null if no such repository exists.
     * @param id the repository id.
     * @return the commits of the repository.
     */
    Collection<? extends ICommit> getCommitsFromRepository(int id);

    /**
     * Gets a subset of the saved commits from a branch of a repository. Returns {@code null} if no such repository or
     * branch exists.
     * @param repositoryId the repository id.
     * @param branchName the branch name. Cannot be {@code null}.
     * @param commitDateStart the start date of the requested commits. Cannot be {@code null}.
     * @param commitDateEnd the end date of the requested commits. Cannot be {@code null}.
     * @return a list with the requested commits.
     */
    List<? extends ICommit> getCommitsFromBranchTimeFrame(int repositoryId, @NotNull String branchName,
                                                          @NotNull LocalDateTime commitDateStart,
                                                          @NotNull LocalDateTime commitDateEnd);

    /**
     * Gets the saved commit of the hash. Returns null if no such commit is saved.
     * @param commitHash the commit hash. Throws NullPointerException if this is null.
     * @return the commit.
     */
    ICommit getCommit(@NotNull String commitHash);
}
