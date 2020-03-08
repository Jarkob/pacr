package pacr.webapp_backend.database;

import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for RepositoryDB.
 *
 * @author Pavel Zwerschke
 */
public class GitTrackingDBTest extends SpringBootTestWithoutShell {

    /**
     * Protected instead of private so subclasses can access these objects.
     */
    protected GitTrackingDB gitTrackingDB;
    protected GitRepository repository;
    protected String commitHash;
    protected GitCommit commit;

    @Autowired
    public GitTrackingDBTest(final GitTrackingDB gitTrackingDB) {
        this.gitTrackingDB = gitTrackingDB;
    }


    /**
     * Creates a repository and a commit.
     */
    @BeforeEach
    public void setUp() {
        // repository
        repository = new GitRepository(true, "pullURL", "RepositoryName",
                "#000000", LocalDate.now());
        final Set<String> selectedBranches = new HashSet<>(Arrays.asList("branch1", "branch2"));
        repository.setSelectedBranches(selectedBranches);

        // commit
        commitHash = "ceacfa7445953cbc8860ddabc55407430a9ca5c3";
        final String message = "commit message";
        final LocalDateTime commitDate = LocalDateTime.now();
        final LocalDateTime authorDate = LocalDateTime.now();
        final GitRepository repositoryForCommit = new GitRepository();
        final GitBranch branch = new GitBranch("test branch");

        commit = new GitCommit(commitHash, message, commitDate, authorDate, repositoryForCommit);

        commit.addLabel("Label1");
        commit.addLabel("Label2");
    }

    /**
     * Adds a repository and gets it again and asserts that the entries are stored.
     */
    @Test
    public void addRepository() {
        assertFalse(repository.isInDatabase());
        final int id = gitTrackingDB.addRepository(repository);

        assertTrue(repository.isInDatabase());

        assertEquals(id, repository.getId());
        final GitRepository fromDB = gitTrackingDB.getRepository(id);

        assertEquals(repository.getId(), fromDB.getId());
        assertEquals(repository.getColor(), fromDB.getColor());
        assertEquals(repository.getName(), fromDB.getName());
        // for this to work the time zone must be set correctly in sql
        assertEquals(repository.getObserveFromDate(), fromDB.getObserveFromDate());
        assertEquals(repository.getPullURL(), fromDB.getPullURL());
    }

    /**
     * Stores a repository and gets all repositories. Asserts that there is only one repository present.
     */
    @Test
    public void getAllRepositories() {
        final int id = gitTrackingDB.addRepository(repository);
        final Collection<GitRepository> repositoriesFromDB = gitTrackingDB.getAllRepositories();
        assertEquals(1, repositoriesFromDB.size());
    }

    /**
     * Adds a repository and removes it again.
     */
    @Test
    public void removeRepository() {
        final int id = gitTrackingDB.addRepository(repository);
        final GitRepository fromDB = gitTrackingDB.getRepository(id);

        gitTrackingDB.removeRepository(id);

        assertNull(gitTrackingDB.getRepository(id));
    }

    /**
     * Updates a repository and asserts that the values are being updated.
     */
    @Test
    public void updateRepository() {
        final int id = gitTrackingDB.addRepository(repository);

        GitRepository fromDB = gitTrackingDB.getRepository(id);

        assertEquals(repository.getName(), fromDB.getName());

        final String newName = "New name";
        repository.setName(newName);

        // old repository from DB not updated
        assertNotEquals(newName, fromDB.getName());

        // load repository from db again
        fromDB = gitTrackingDB.getRepository(id);
        assertNotEquals(newName, fromDB.getName());

        // load updated repository
        gitTrackingDB.updateRepository(repository);

        fromDB = gitTrackingDB.getRepository(id);
        assertEquals(newName, fromDB.getName());
    }

    /**
     * Adds a commit to the database and asserts that the values are being stored in the database.
     */
    @Test
    public void addCommits() {
        final GitRepository repository = new GitRepository(false, "git@github.com:leanprover/lean.git",
                "testingrepo", "#000000", null);
        final String commitHash = "ceacfa7445953cbc8860ddabc55407430a9ca5c3";

        // first add repository to DB
        gitTrackingDB.addRepository(repository);

        GitCommit parent = new GitCommit(commitHash,
                "commited", LocalDateTime.now(), LocalDateTime.now(), repository);

        gitTrackingDB.addCommits(new HashSet<>(Collections.singletonList(parent)));

        final int amountCommits = 3;
        for (int i = 0; i < amountCommits - 1; i++) {
            final GitCommit child = new GitCommit(commitHash + i, "commited", LocalDateTime.now(), LocalDateTime.now(),
                    repository);
            child.addParent(parent.getCommitHash());

            gitTrackingDB.addCommits(new HashSet<>(Collections.singletonList(child)));

            parent = child;
        }

        // getting all commits with repository ID
        final Collection<GitCommit> commits = gitTrackingDB.getAllCommits(repository.getId());
        assertEquals(amountCommits, commits.size());

        // getting commit with commitHash
        final GitCommit fromDB = gitTrackingDB.getCommit(commitHash);
    }

    /**
     * CommitDB is unable to add the commit to the database because the repository is not stored in the database yet.
     */
    @Test
    public void unableToAddCommitToDatabase() {
        commit.setRepository(new GitRepository());
        assertThrows(RepositoryNotStoredException.class,
                () -> gitTrackingDB.addCommits(new HashSet<>(Collections.singletonList(commit))));
    }

    @AfterEach
    public void cleanUp() {
        for (final GitRepository repository : gitTrackingDB.getAllRepositories()) {
            gitTrackingDB.removeRepository(repository.getId());
        }
    }

}
