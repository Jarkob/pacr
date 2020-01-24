package pacr.webapp_backend.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GetCommitDBTest extends GitTrackingDBTest {

    private static final int EXPECTED_NUM_OF_COMMITS_IN_REPOSITORY = 1;
    private static final int EXPECTED_NUM_OF_COMMITS_ON_BRANCH = 1;
    private static final int EXPECTED_NUM_OF_ALL_COMMITS = 1;
    private static final String BRANCH_NAME = "branch";

    private GetCommitDB getCommitDB;

    @Autowired
    public GetCommitDBTest(GitTrackingDB gitTrackingDB, GetCommitDB getCommitDB) {
        super(gitTrackingDB);
        this.getCommitDB = getCommitDB;
    }

    /**
     * Tests whether getCommitsFromRepository returns the correct amount of commits.
     */
    @Test
    public void getCommitsFromRepository_repositoryWithCommits_shouldReturnAllCommitsInRepository() {
        super.gitTrackingDB.addRepository(repository);

        commit.setRepository(repository);

        repository.addNewCommit(commit);
        super.gitTrackingDB.addCommit(commit);

        assertEquals(EXPECTED_NUM_OF_COMMITS_IN_REPOSITORY,
                getCommitDB.getCommitsFromRepository(repository.getId()).size());
    }

    /**
     * Tests whether getCommitsFromBranch returns the correct amount of commits.
     */
    @Test
    public void getCommitsFromBranch_branchWithCommits_shouldReturnAllCommitsOnBranch() {
        GitBranch branch = new GitBranch(BRANCH_NAME);
        repository.addBranchToSelection(branch);

        super.gitTrackingDB.addRepository(repository);

        commit.setRepository(repository);
        commit.addBranch(branch);

        repository.addNewCommit(commit);
        super.gitTrackingDB.addCommit(commit);

        assertEquals(EXPECTED_NUM_OF_COMMITS_ON_BRANCH,
                getCommitDB.getCommitsFromBranch(repository.getId(), BRANCH_NAME).size());
    }

    /**
     * Tests whether getAllCommit returns the correct amount of commits.
     */
    @Test
    public void getAllCommits_savedCommits_shouldReturnAllCommits() {
        super.gitTrackingDB.addRepository(repository);

        commit.setRepository(repository);

        repository.addNewCommit(commit);
        super.gitTrackingDB.addCommit(commit);

        assertEquals(EXPECTED_NUM_OF_ALL_COMMITS, getCommitDB.getAllCommits().size());
    }
}
