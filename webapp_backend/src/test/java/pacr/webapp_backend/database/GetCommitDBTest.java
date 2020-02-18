package pacr.webapp_backend.database;

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

    /**
     * Tests whether getCommitsFromBranch only gets the first 200 newest if a pageable request is send.
     */
    @Test
    public void getCommitsFromBranch_pageable_shouldOnlyReturnOnePage() {
        Set<String> branches = new HashSet<>();
        branches.add(BRANCH_NAME);
        repository.setSelectedBranches(branches);

        GitBranch branch = repository.getTrackedBranch(BRANCH_NAME);

        gitTrackingDB.addRepository(repository);

        for (int i = 0; i < PAGE_SIZE + 1; ++i) {
            GitCommit newCommit = new GitCommit(HASH_TWO + i, MSG, LocalDateTime.now().plusSeconds(i),
                    LocalDateTime.now(), repository);
            newCommit.addBranch(branch);

            gitTrackingDB.addCommits(new HashSet<>(Arrays.asList(newCommit)));
        }

        List<? extends ICommit> commits = getCommitDB
                .getCommitsFromBranch(repository.getId(), branch.getName(), 0, PAGE_SIZE)
                .getContent();

        List<? extends ICommit> firstCommit = getCommitDB
                .getCommitsFromBranch(repository.getId(), branch.getName(), 1, PAGE_SIZE)
                .getContent();

        assertEquals(PAGE_SIZE, commits.size());
        assertEquals(1, firstCommit.size());

        boolean firstPageContainsFirstCommit = false;

        LocalDateTime previousTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(PAGE_SIZE + 1);

        for (ICommit commit : commits) {
            if (commit.getCommitHash().equals(HASH_TWO + 0)) {
                firstPageContainsFirstCommit = true;
            }
            LocalDateTime currentTime = commit.getCommitDate();
            assertTrue(currentTime.isBefore(previousTime) || currentTime.equals(previousTime),
                    "commit " + commit.getCommitHash() + ": " + currentTime.toString() + " is not before "
                            + previousTime.toString());
            previousTime = currentTime;
        }

        assertFalse(firstPageContainsFirstCommit);
        assertEquals(HASH_TWO + 0, firstCommit.get(0).getCommitHash());
    }

    @Test
    void getCommitsFromBranch_pageableAndUnknownBranch_shouldReturnNull() {
        gitTrackingDB.addRepository(repository);
        Page<? extends ICommit> commits = getCommitDB.getCommitsFromBranch(repository.getId(), UNKNOWN_BRANCH, 0,
                PAGE_SIZE);

        assertNull(commits);
    }
}
