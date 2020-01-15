package pacr.webapp_backend.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.git_tracking.Branch;
import pacr.webapp_backend.git_tracking.Commit;
import pacr.webapp_backend.git_tracking.Repository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for CommitDB
 *
 * @author Pavel Zwerschke
 */
@SpringBootTest
public class CommitDBTest {

    private CommitDB commitDB;

    private String commitHash = "c4c5cc";
    private Commit commit;

    @Autowired
    public CommitDBTest(CommitDB commitDB) {
        this.commitDB = commitDB;
    }

    /**
     * Deletes the entries in the database and creates a commit.
     */
    @BeforeEach
    public void setUp() {
        commitDB.deleteAll();

        commitHash = "c4c5cc";
        String message = "commit message";
        LocalDate commitDate = LocalDate.now();
        LocalDate authorDate = LocalDate.now();
        Set<Commit> parents = new HashSet<>();
        Repository repository = new Repository();
        repository.setId(20);
        Branch branch = new Branch("test branch");

        commit = new Commit(commitHash, message, commitDate, authorDate, parents, repository, branch);

        commit.addLabel("Label1");
        commit.addLabel("Label2");
    }

    /**
     * Adds a commit to the database and asserts that the values are being stored in the database.
     */
    @Test
    public void addCommit() {
        commitDB.addCommit(commit);

        Commit fromDB = commitDB.getCommit(commitHash);
        assertEquals(commit.getMessage(), fromDB.getMessage());
        assertEquals(commit.getAuthorDate(), fromDB.getAuthorDate());
        for (String label : commit.getLabels()) {
            assertTrue(fromDB.getLabels().contains(label));
        }
        assertEquals(commit.getParents(), fromDB.getParents());
    }

    /**
     * CommitDB is unable to add the commit to the database because the repository is not stored in the database yet.
     */
    @Test
    public void unableToAddCommitToDatabase() {
        commit.setRepository(new Repository());
        assertThrows(RepositoryNotStoredException.class, () -> commitDB.addCommit(commit));
    }

}
