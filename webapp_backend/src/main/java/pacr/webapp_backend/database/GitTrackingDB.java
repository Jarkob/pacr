package pacr.webapp_backend.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * Implementation for IGitTrackingAccess. Primary bean for dependency injection of this class type.
 *
 * @author Pavel Zwerschke
 */
@Primary
@Component
public class GitTrackingDB extends CommitRepositoryDB implements IGitTrackingAccess {

    private static final Logger LOGGER = LogManager.getLogger(GitTrackingDB.class);

    /**
     * Creates a new instance of GitTrackingDB.
     * @param commitDB is the JPA commit access interface.
     * @param repositoryDB is the JPA repository access interface.
     */
    public GitTrackingDB(@NotNull final CommitDB commitDB, @NotNull final RepositoryDB repositoryDB) {
        super(commitDB, repositoryDB);
    }

    @Override
    public void addCommit(@NotNull final GitCommit commit) {
        Objects.requireNonNull(commit);

        if (!commit.repositoryIsInDatabase()) {
            throw new RepositoryNotStoredException("The repository of the commit must be stored in the database "
                    + "before this commit is being stored in the database.");
        }
        commitDB.save(commit);
    }

    @Override
    public void addCommits(@NotNull final Set<GitCommit> commits) {
        Objects.requireNonNull(commits);

        commitDB.saveAll(commits);
    }

    @Override
    public void updateCommit(@NotNull final GitCommit commit) {
        Objects.requireNonNull(commit);

        if (!commit.repositoryIsInDatabase()) {
            throw new RepositoryNotStoredException("The repository of the commit must be stored in the database "
                    + "before this commit is being stored in the database.");
        }
        if (!commitDB.existsById(commit.getCommitHash())) {
            throw new IllegalArgumentException("Commit doesn't exist.");
        }
        commitDB.save(commit);
    }

    @Override
    public void updateCommits(@NotNull final Set<GitCommit> commits) {
        Objects.requireNonNull(commits);

        commitDB.saveAll(commits);
    }

    @Override
    public Collection<GitCommit> getAllCommits(final int repositoryID) {
        return commitDB.findGitCommitsByRepository_Id(repositoryID);
    }

    @Override
    public Page<GitCommit> getAllCommits(final int repositoryID, final Pageable pageable) {
        return commitDB.findAllByRepository_Id(repositoryID, pageable);
    }

    @Override
    public Set<String> getAllCommitHashes(final int repositoryID) {
        final Collection<GitCommit> commits = commitDB.findGitCommitsByRepository_Id(repositoryID);

        final Set<String> commitHashes = new HashSet<>();
        for (final GitCommit commit : commits) {
            commitHashes.add(commit.getCommitHash());
        }

        return commitHashes;
    }

    @Override
    public Set<GitCommit> getCommits(@NotNull Set<String> commitHashes) {
        return commitDB.findGitCommitsByCommitHashIn(commitHashes);
    }

    @Override
    public GitCommit getCommit(@NotNull String commitHash) {
        Objects.requireNonNull(commitHash);

        return commitDB.findById(commitHash).orElse(null);
    }

    @Override
    public void removeCommits(@NotNull final Set<String> commitHashes) {
        Objects.requireNonNull(commitHashes);

        commitDB.removeGitCommitsByCommitHashIn(commitHashes);
    }

    @Override
    public List<GitRepository> getAllRepositories() {
        return repositoryDB.findAllByOrderByName();
    }

    @Override
    public GitRepository getRepository(final int repositoryID) {
        return repositoryDB.findById(repositoryID).orElse(null);
    }

    @Override
    public int addRepository(@NotNull final GitRepository repository) {
        Objects.requireNonNull(repository);

        return repositoryDB.save(repository).getId();
    }

    @Override
    public void removeRepository(final int repositoryID) {
        final GitRepository repository = getRepository(repositoryID);
        if (repository == null) {
            throw new NoSuchElementException("Repository with ID " + repositoryID + " was not found.");
        }

        LOGGER.info("Deleting commits belonging to repository with ID {}.", repositoryID);

        commitDB.removeGitCommitsByRepository_Id(repositoryID);

        LOGGER.info("Deleting repository with ID {}.", repositoryID);

        repositoryDB.delete(repository);
    }

    @Override
    public void updateRepository(@NotNull final GitRepository repository) throws NoSuchElementException {
        Objects.requireNonNull(repository);

        if (getRepository(repository.getId()) == null) {
            throw new NoSuchElementException("Repository with ID " + repository.getId() + " was not found.");
        }

        addRepository(repository);
    }

    @Override
    public boolean containsCommit(@NotNull final String commitHash) {
        Objects.requireNonNull(commitHash);

        return commitDB.existsById(commitHash);
    }

}
