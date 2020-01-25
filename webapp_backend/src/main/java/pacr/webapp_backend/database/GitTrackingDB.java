package pacr.webapp_backend.database;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Implementation for IGitTrackingAccess. Primary bean for dependency injection of this class type.
 *
 * @author Pavel Zwerschke
 */
@Primary
@Component
public class GitTrackingDB extends CommitRepositoryDB implements IGitTrackingAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitTrackingDB.class);

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

        //todo delete results
        commitDB.deleteById(commitHash);
    }

    @Override
    public Collection<GitRepository> getAllRepositories() {
        Collection<GitRepository> repositories = new HashSet<>();

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
    public void removeRepository(int repositoryID) throws NotFoundException {
        GitRepository repository = getRepository(repositoryID);
        if (repository == null) {
            throw new NotFoundException("Repository with ID " + repositoryID + " was not found.");
        }

        LOGGER.info("Deleting commits belonging to repository with ID {}.", repositoryID);

        for (GitCommit commit : getAllCommits(repositoryID)) {
            removeCommit(commit.getCommitHash());
        }

        LOGGER.info("Deleting repository with ID {}.", repositoryID);

        repositoryDB.delete(repository);
    }

    @Override
    public void updateRepository(@NotNull GitRepository repository) throws NotFoundException {
        Objects.requireNonNull(repository);

        if (getRepository(repository.getId()) == null) {
            throw new NotFoundException("Repository with ID " + repository.getId() + " was not found.");
        }

        addRepository(repository);
    }

    @Override
    public boolean containsCommit(@NotNull String commitHash) {
        Objects.requireNonNull(commitHash);

        return commitDB.existsById(commitHash);
    }

    @Override
    public GitCommit getHead() { // todo implement
        return null;
    }

    @Override
    public GitCommit getHead(String branch) { // todo implement
        return null;
    }

    // todo works better with an sql query
    @Override
    public Collection<String> getAllCommitHashes(int repositoryID) {
        Collection<GitCommit> gitCommits = getAllCommits(repositoryID);

        Collection<String> commitHashes = new HashSet<>();

        for (GitCommit commit : gitCommits) {
            commitHashes.add(commit.getCommitHash());
        }
        return commitHashes;
    }

}
