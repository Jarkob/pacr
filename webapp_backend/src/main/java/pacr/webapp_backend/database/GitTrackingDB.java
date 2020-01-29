package pacr.webapp_backend.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import javax.validation.constraints.NotNull;
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
    public GitTrackingDB(@NotNull CommitDB commitDB, @NotNull RepositoryDB repositoryDB) {
        super(commitDB, repositoryDB);
    }

    @Override
    public void addCommit(@NotNull GitCommit commit) {
        Objects.requireNonNull(commit);

        if (!commit.repositoryIsInDatabase()) {
            throw new RepositoryNotStoredException("The repository of the commit must be stored in the database "
                    + "before this commit is being stored in the database.");
        }
        commitDB.save(commit);
    }

    @Override
    public Collection<GitCommit> getAllCommits(int repositoryID) {
        return commitDB.findCommitsByRepository_Id(repositoryID);
    }

    @Override
    public GitCommit getCommit(@NotNull String commitHash) {
        Objects.requireNonNull(commitHash);

        return commitDB.findById(commitHash).orElse(null);
    }

    @Override
    public void removeCommit(@NotNull String commitHash) {
        Objects.requireNonNull(commitHash);

        commitDB.deleteById(commitHash);
    }

    @Override
    public Set<GitRepository> getAllRepositories() {
        Set<GitRepository> repositories = new HashSet<>();

        Iterable<GitRepository> iterable = repositoryDB.findAll();
        iterable.forEach(repositories::add);

        return repositories;
    }

    @Override
    public GitRepository getRepository(int repositoryID) {
        return repositoryDB.findById(repositoryID).orElse(null);
    }

    @Override
    public int addRepository(@NotNull GitRepository repository) {
        Objects.requireNonNull(repository);

        return repositoryDB.save(repository).getId();
    }

    @Override
    public void removeRepository(int repositoryID) {
        GitRepository repository = getRepository(repositoryID);
        if (repository == null) {
            throw new NoSuchElementException("Repository with ID " + repositoryID + " was not found.");
        }

        LOGGER.info("Deleting commits belonging to repository with ID {}.", repositoryID);

        for (GitCommit commit : getAllCommits(repositoryID)) {
            removeCommit(commit.getCommitHash());
        }

        LOGGER.info("Deleting repository with ID {}.", repositoryID);

        repositoryDB.delete(repository);
    }

    @Override
    public void updateRepository(@NotNull GitRepository repository) throws NoSuchElementException {
        Objects.requireNonNull(repository);

        if (getRepository(repository.getId()) == null) {
            throw new NoSuchElementException("Repository with ID " + repository.getId() + " was not found.");
        }

        addRepository(repository);
    }

    @Override
    public boolean containsCommit(@NotNull String commitHash) {
        Objects.requireNonNull(commitHash);

        return commitDB.existsById(commitHash);
    }

}
