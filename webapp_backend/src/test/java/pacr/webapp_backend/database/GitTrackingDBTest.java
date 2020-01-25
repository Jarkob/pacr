package pacr.webapp_backend.database;

import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for RepositoryDB.
 *
 * @author Pavel Zwerschke
 */
@SpringBootTest
public class GitTrackingDBTest {

    /**
     * Protected instead of private so subclasses can access these objects.
     */
    protected GitTrackingDB gitTrackingDB;
    protected GitRepository repository;
    protected String commitHash;
    protected GitCommit commit;

    @Autowired
    public GitTrackingDBTest(GitTrackingDB gitTrackingDB) {
        this.gitTrackingDB = gitTrackingDB;
    }


    /**
     * Creates a repository and a commit.
     */
    @BeforeEach
    public void setUp() {
        // repository
        Set<GitBranch> selectedBranches = new HashSet<>();
        selectedBranches.add(new GitBranch("branch1"));
        selectedBranches.add(new GitBranch("branch2"));
        repository = new GitRepository(true, selectedBranches, "pullURL", "RepositoryName",
                new Color(0, 0, 0), LocalDate.now());

        // commit
        commitHash = "ceacfa7445953cbc8860ddabc55407430a9ca5c3";
        String message = "commit message";
        LocalDateTime commitDate = LocalDateTime.now();
        LocalDateTime authorDate = LocalDateTime.now();
        GitRepository repositoryForCommit = new GitRepository();
        GitBranch branch = new GitBranch("test branch");

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
        int id = gitTrackingDB.addRepository(repository);

        assertTrue(repository.isInDatabase());

        assertEquals(id, repository.getId());
        GitRepository fromDB = gitTrackingDB.getRepository(id);

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
        int id = gitTrackingDB.addRepository(repository);
        Collection<GitRepository> repositoriesFromDB = gitTrackingDB.getAllRepositories();
        assertEquals(1, repositoriesFromDB.size());
    }

    /**
     * Adds a repository and removes it again.
     */
    @Test
    public void removeRepository() {
        int id = gitTrackingDB.addRepository(repository);
        GitRepository fromDB = gitTrackingDB.getRepository(id);

        gitTrackingDB.removeRepository(id);

        assertNull(gitTrackingDB.getRepository(id));
    }

    /**
     * Updates a repository and asserts that the values are being updated.
     */
    @Test
    public void updateRepository() {
        int id = gitTrackingDB.addRepository(repository);

        GitRepository fromDB = gitTrackingDB.getRepository(id);

        assertEquals(repository.getName(), fromDB.getName());

        String newName = "New name";
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
    public void addCommit() throws NotFoundException {
        GitRepository repository = new GitRepository(false, new HashSet<>(), "git@github.com:leanprover/lean.git",
                "testingrepo", new Color(0x00000), null);
        String commitHash = "ceacfa7445953cbc8860ddabc55407430a9ca5c3";

        // first add repository to DB
        gitTrackingDB.addRepository(repository);

        GitCommit parent = new GitCommit(commitHash,
                "commited suicide", LocalDateTime.now(), LocalDateTime.now(), repository);

        int amountCommits = 3;
        for (int i = 0; i < amountCommits - 1; i++) {
            GitCommit child = new GitCommit(commitHash + i, "commited", LocalDateTime.now(), LocalDateTime.now(),
                    repository);
            child.addParent(parent.getCommitHash());

            parent = child;
        }

        // then add commit to DB
        gitTrackingDB.updateRepository(repository);

        // getting all commits with repository ID
        Collection<GitCommit> commits = gitTrackingDB.getAllCommits(repository.getId());
        assertEquals(amountCommits, commits.size());

        // getting commit with commitHash
        GitCommit fromDB = gitTrackingDB.getCommit(commitHash);
    }

    /**
     * CommitDB is unable to add the commit to the database because the repository is not stored in the database yet.
     */
    @Test
    public void unableToAddCommitToDatabase() {
        commit.setRepository(new GitRepository());
        assertThrows(RepositoryNotStoredException.class, () -> gitTrackingDB.addCommit(commit));
    }

    @AfterEach
    public void cleanUp() {
        for (GitRepository repository : gitTrackingDB.getAllRepositories()) {
            gitTrackingDB.removeRepository(repository.getId());
        }
    }

}
