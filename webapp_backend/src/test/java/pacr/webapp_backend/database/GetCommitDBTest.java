package pacr.webapp_backend.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.shared.ICommit;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetCommitDBTest extends GitTrackingDBTest {

    private static final int EXPECTED_NUM_OF_COMMITS_IN_REPOSITORY = 1;
    private static final int EXPECTED_NUM_OF_COMMITS_ON_BRANCH = 1;
    private static final int EXPECTED_NUM_OF_ALL_COMMITS = 1;
    private static final String BRANCH_NAME = "branch";
    private static final String BRANCH_NAME_TWO = "branch2";
    private static final String HASH_TWO = "hash2";
    private static final String MSG = "message";

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
        /*
        super.gitTrackingDB.addRepository(repository);

        commit.setRepository(repository);

        repository.addNewCommit(commit);
        super.gitTrackingDB.addCommit(commit);

        assertEquals(EXPECTED_NUM_OF_COMMITS_IN_REPOSITORY,
                getCommitDB.getCommitsFromRepository(repository.getId()).size());

         */
    }

    /**
     * Tests whether getCommitsFromBranch returns the correct amount of commits.
     */
    @Test
    public void getCommitsFromBranch_branchWithCommits_shouldReturnAllCommitsOnBranch() {
        /*
        GitBranch branch = new GitBranch(BRANCH_NAME);
        GitBranch branch2 = new GitBranch(BRANCH_NAME_TWO);
        repository.addBranchToSelection(branch);
        repository.addBranchToSelection(branch2);

        super.gitTrackingDB.addRepository(repository);

        commit.setRepository(repository);
        commit.addBranch(branch);

        GitCommit commit2 = new GitCommit(HASH_TWO, MSG, LocalDateTime.now(), LocalDateTime.now(), repository);
        commit2.addBranch(branch2);

        repository.addNewCommit(commit);
        repository.addNewCommit(commit2);

        super.gitTrackingDB.updateRepository(repository);
        super.gitTrackingDB.addCommit(commit);
        super.gitTrackingDB.addCommit(commit2);

        assertEquals(EXPECTED_NUM_OF_COMMITS_ON_BRANCH,
                getCommitDB.getCommitsFromBranch(repository.getId(), BRANCH_NAME).size());

         */
    }

    /**
     * Tests whether getAllCommit returns the correct amount of commits.
     */
    @Test
    public void getAllCommits_savedCommits_shouldReturnAllCommits() {
        /*
        super.gitTrackingDB.addRepository(repository);

        commit.setRepository(repository);

        repository.addNewCommit(commit);
        super.gitTrackingDB.addCommit(commit);

        assertEquals(EXPECTED_NUM_OF_ALL_COMMITS, getCommitDB.getAllCommits().size());

         */
    }
}
