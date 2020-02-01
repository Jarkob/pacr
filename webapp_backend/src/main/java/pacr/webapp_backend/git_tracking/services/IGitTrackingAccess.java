package pacr.webapp_backend.git_tracking.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents the interface for storing and accessing
 * the data for Git Tracking.
 *
 * @author Pavel Zwerschke
 */
public interface IGitTrackingAccess {

    /**
     * Returns all repositories.
     * @return repositories.
     */
    Set<GitRepository> getAllRepositories();

    /**
     * Finds a repository by its repositoryID.
     * @param repositoryID is the repositoryID.
     * @return repository if it was found or else null.
     */
    GitRepository getRepository(int repositoryID);

    /**
     * Stores a repository.
     * @param repository is the repository being stored.
     * @return the ID of the repository.
     */
    int addRepository(GitRepository repository);

    /**
     * Deletes a repository.
     * @param repositoryID is the ID of the repository being deleted.
     * @throws NoSuchElementException when the repository was not found.
     */
    void removeRepository(int repositoryID) throws NoSuchElementException;

    /**
     * Updates a repository.
     * @param repository is the repository being updated.
     * @throws NoSuchElementException when the repository was not found.
     */
    void updateRepository(@NotNull GitRepository repository) throws NoSuchElementException;

    /**
     * Stores a commit. The repository to which the commit
     * belongs to must already be stored already in the database.
     * @param commit is the commit being stored.
     */
    void addCommit(@NotNull GitCommit commit);

    void addCommits(@NotNull Set<GitCommit> commits);

    void updateCommit(@NotNull GitCommit commit);

    void updateCommits(@NotNull Set<GitCommit> commits);

    /**
     * Returns all commits belonging to a repository.
     * @param repositoryID is the ID of the repository.
     * @return all commits belonging to the repository.
     */
    Collection<GitCommit> getAllCommits(int repositoryID);

    Page<GitCommit> getAllCommits(int repositoryID, Pageable pageable);

    /**
     * Returns all commit hashes of commits belonging to a repository.
     * @param repositoryID is the ID of the repository.
     * @return all commit hashes of commits belonging to the repository.
     */
    Collection<String> getAllCommitHashes(int repositoryID);

    /**
     * Returns a commit.
     * @param commitHash is the commit hash of the commit.
     * @return the commit with this commit hash or null if not found.
     */
    GitCommit getCommit(@NotNull String commitHash);

    /**
     * Removes a commit.
     * @param commitHash is the commit hash of the commit.
     */
    void removeCommit(@NotNull String commitHash);

    /**
     * Checks if a commit is in the database or not.
     * @param commitHash is the commit hash.
     * @return true if the commit is already in the database,
     * false if not.
     */
    boolean containsCommit(@NotNull String commitHash);
}
