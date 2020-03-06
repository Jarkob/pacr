package pacr.webapp_backend.database;

import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.shared.ICommit;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GetCommitDBTest extends GitTrackingDBTest {

    private static final int EXPECTED_NUM_OF_COMMITS_IN_REPOSITORY = 1;
    private static final int EXPECTED_NUM_OF_COMMITS_ON_BRANCH = 1;
    private static final int EXPECTED_NUM_OF_ALL_COMMITS = 1;
    private static final String BRANCH_NAME = "branch";
    private static final String BRANCH_NAME_TWO = "branch2";
    private static final String HASH_TWO = "hash2";
    private static final String MSG = "message";
    private static final int PAGE_SIZE = 200;
    private static final int UNKNOWN_REPO = 124981;
    private static final String UNKNOWN_BRANCH = "124981";

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

        super.gitTrackingDB.addCommits(new HashSet<>(Arrays.asList(commit)));

        assertEquals(EXPECTED_NUM_OF_COMMITS_IN_REPOSITORY,
                getCommitDB.getCommitsFromRepository(repository.getId()).size());
    }

    @Test
    void getCommitsFromBranch_pageableAndUnknownBranch_shouldReturnNull() {
        gitTrackingDB.addRepository(repository);
        List<? extends ICommit> commits = getCommitDB.getCommitsFromBranchTimeFrame(repository.getId(), UNKNOWN_BRANCH,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(1));

        assertNull(commits);
    }

    @Test
    void getCommitsFromBranchTimeFrame_shouldOnlyReturnCommitsInTime() {
        repository.createBranchIfNotExists(BRANCH_NAME);

        GitBranch branch = repository.getTrackedBranch(BRANCH_NAME);

        gitTrackingDB.addRepository(repository);

        GitCommit newCommitOne = new GitCommit(HASH_TWO, MSG,
                LocalDateTime.of(2020, 3, 1, 0, 0),
                LocalDateTime.now(), repository);
        newCommitOne.addBranch(branch);

        GitCommit newCommitTwo = new GitCommit(HASH_TWO + 1, MSG,
                LocalDateTime.of(2020, 2, 1, 0, 0),
                LocalDateTime.now(), repository);
        newCommitTwo.addBranch(branch);

        LocalDateTime startTime = LocalDateTime.now();

        HashSet<GitCommit> commits = new HashSet<>();

        for (int i = 0; i < PAGE_SIZE + 1; ++i) {
            GitCommit newCommit = new GitCommit(HASH_TWO + i, MSG, startTime.plusSeconds(i),
                    LocalDateTime.now(), repository);
            newCommit.addBranch(branch);

            commits.add(newCommit);
        }

        gitTrackingDB.addCommits(commits);

        List<? extends ICommit> commitsOutput = getCommitDB
                .getCommitsFromBranchTimeFrame(repository.getId(), branch.getName(),
                        startTime.plusSeconds(PAGE_SIZE / 4),
                        startTime.plusSeconds(PAGE_SIZE / 2));

        for (ICommit commit : commitsOutput) {
            LocalDateTime currentTime = commit.getCommitDate();
            assertTrue(currentTime.isAfter(startTime.plusSeconds(PAGE_SIZE / 4))
                    && currentTime.isBefore(startTime.plusSeconds(PAGE_SIZE / 2)));
        }
    }
}
