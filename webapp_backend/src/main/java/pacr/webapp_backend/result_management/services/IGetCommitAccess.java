package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.Collection;

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
     * Gets all saved commits from a branch of a repository. Returns null if no such repository or branch exists.
     * @param id the repository id.
     * @param branch the branch name. Throws IllegalArgumentException if this is null.
     * @return the commits of the branch.
     */
    Collection<? extends ICommit> getCommitsFromBranch(int id, @NotNull String branch);

    /**
     * Gets all saved commits. Returns null if no commits are saved.
     * @return all commits.
     */
    Collection<? extends ICommit> getAllCommits();

    /**
     * Gets the saved commit of the hash. Returns null if no such commit is saved.
     * @param commitHash the commit hash. Throws NullPointerException if this is null.
     * @return the commit.
     */
    ICommit getCommit(@NotNull String commitHash);
}
