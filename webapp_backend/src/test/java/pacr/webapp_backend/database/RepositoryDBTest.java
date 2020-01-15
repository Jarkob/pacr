package pacr.webapp_backend.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.git_tracking.Branch;
import pacr.webapp_backend.git_tracking.Repository;

import java.awt.*;
import java.time.LocalDate;
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
public class RepositoryDBTest {

    private RepositoryDB repositoryDB;

    private Repository repository;

    @Autowired
    public RepositoryDBTest(RepositoryDB repositoryDB) {
        this.repositoryDB = repositoryDB;
    }

    /**
     * Deletes all entries in the database and creates a repository with two branches being in selectedBranches.
     */
    @BeforeEach
    public void setUp() {
        repositoryDB.deleteAll();

        Set<Branch> selectedBranches = new HashSet<>();
        selectedBranches.add(new Branch("branch1"));
        selectedBranches.add(new Branch("branch2"));
        repository = new Repository(true, selectedBranches, "pullURL", "RepositoryName",
                new Color(0, 0, 0), LocalDate.now());
    }

    /**
     * Adds a repository and gets it again and asserts that the entries are stored.
     */
    @Test
    public void addRepository() {
        assertFalse(repository.isInDatabase());
        int id = repositoryDB.addRepository(repository);
        assertTrue(repository.isInDatabase());

        assertEquals(id, repository.getId());
        Repository fromDB = repositoryDB.getRepository(id);

        assertEquals(repository.getId(), fromDB.getId());
        assertEquals(repository.getColor(), fromDB.getColor());
        assertEquals(repository.getName(), fromDB.getName());
        assertEquals(repository.getObserveFromDate(), fromDB.getObserveFromDate());
        assertEquals(repository.getPullURL(), fromDB.getPullURL());
    }

    /**
     * Stores a repository and gets all repositories. Asserts that there is only one repository present.
     */
    @Test
    public void getAllRepositories() {
        int id = repositoryDB.addRepository(repository);
        Collection<Repository> repositoriesFromDB = repositoryDB.getAllRepositories();
        assertEquals(1, repositoriesFromDB.size());
    }

    /**
     * Adds a repository and removes it again.
     */
    @Test
    public void removeRepository() {
        int id = repositoryDB.addRepository(repository);
        Repository fromDB = repositoryDB.getRepository(id);

        repositoryDB.removeRepository(id);

        assertNull(repositoryDB.getRepository(id));
    }

    /**
     * Updates a repository and asserts that the values are being updated.
     */
    @Test
    public void updateRepository() {
        int id = repositoryDB.addRepository(repository);

        Repository fromDB = repositoryDB.getRepository(id);

        assertEquals(repository.getName(), fromDB.getName());

        String newName = "New name";
        repository.setName(newName);

        // old repository from DB not updated
        assertNotEquals(newName, fromDB.getName());

        // load repository from db again
        fromDB = repositoryDB.getRepository(id);
        assertNotEquals(newName, fromDB.getName());

        // load updated repository
        repositoryDB.updateRepository(repository);
        fromDB = repositoryDB.getRepository(id);
        assertEquals(newName, fromDB.getName());
    }

}
