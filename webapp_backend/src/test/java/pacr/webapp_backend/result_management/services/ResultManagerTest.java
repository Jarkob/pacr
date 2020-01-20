package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.RepositoryDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.git_tracking.GitBranch;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.shared.IBenchmarkingResult;

import java.awt.Color;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class ResultManagerTest {

    private static final String HASH_TWO = "hash2";
    private static final String MSG = "msg";
    private static final LocalDate NOW = LocalDate.now();
    private static final String BRANCH_NAME = "branch";
    private static final String URL = "url";
    private static final String REPO_NAME = "repo";

    private ResultManager resultManager;
    private ResultDB resultDB;
    private IGitTrackingAccess gitTrackingAccess;
    private RepositoryDB repositoryDB;
    private BenchmarkDB benchmarkDB;

    private GitRepository repository;
    private GitBranch branch;
    private GitCommit commit;

    @Autowired
    public ResultManagerTest(ResultManager resultManager, ResultDB resultDB, IGitTrackingAccess gitTrackingAccess,
                             RepositoryDB repositoryDB, BenchmarkDB benchmarkDB) {
        this.resultManager = resultManager;
        this.resultDB = resultDB;
        this.gitTrackingAccess = gitTrackingAccess;
        this.repositoryDB = repositoryDB;
        this.benchmarkDB = benchmarkDB;
    }


    /**
     * Creates a repository and a commit.
     */
    @BeforeEach
    public void setUp() {
        // repository
        branch = new GitBranch(BRANCH_NAME);
        Set<GitBranch> selectedBranches = new HashSet<>();
        selectedBranches.add(branch);
        repository = new GitRepository(true, selectedBranches, URL, REPO_NAME,
                new Color(0, 0, 0), LocalDate.now()); // TODO change this if the interface changes

        // commit
        commit = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW, new HashSet<>(), repository,
                branch);

        gitTrackingAccess.addRepository(repository);
    }

    @AfterEach
    public void cleanUp() {
        repositoryDB.deleteAll();
        resultDB.deleteAll();
        benchmarkDB.deleteAll();
    }

    /**
     * Tests whether a result saved with saveBenchmarkingResults can be found in the database.
     */
    @Test
    void saveBenchmarkingResults_shouldBeSavedInDatabase() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertNotNull(savedResult);
        assertEquals(SimpleBenchmarkingResult.COMMIT_HASH, savedResult.getCommitHash());
    }

    /**
     * Tests whether multiple results can be saved with importBenchmarkingResults.
     */
    @Test
    void importBenchmarkingResults_shouldBeSavedInDatabase() {
        SimpleBenchmarkingResult resultOne = new SimpleBenchmarkingResult();
        SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        Collection<IBenchmarkingResult> results = new LinkedList<>();
        results.add(resultOne);
        results.add(resultTwo);

        gitTrackingAccess.addCommit(new GitCommit(HASH_TWO, MSG, NOW, NOW, new HashSet<>(), repository, branch));

        resultManager.importBenchmarkingResults(results);

        CommitResult savedResultOne = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);
        CommitResult savedResultTwo = resultDB.getResultFromCommit(HASH_TWO);

        assertNotNull(savedResultOne);
        assertEquals(SimpleBenchmarkingResult.COMMIT_HASH, savedResultOne.getCommitHash());

        assertNotNull(savedResultTwo);
        assertEquals(HASH_TWO, savedResultTwo.getCommitHash());
    }

    /**
     * Tests whether deleteAllResultsForRepository only deletes results from commits in that repository.
     */
    @Test
    void deleteAllResultsForRepository_shouldOnlyDeleteFromRepository() {
        SimpleBenchmarkingResult resultOne = new SimpleBenchmarkingResult();
        SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        Collection<IBenchmarkingResult> results = new LinkedList<>();
        results.add(resultOne);
        results.add(resultTwo);

        GitRepository repoTwo = new GitRepository(false, new HashSet<>(), URL, REPO_NAME, Color.BLACK,
                NOW);
        GitCommit commitTwo = new GitCommit(HASH_TWO, MSG, NOW, NOW, new HashSet<>(), repoTwo,
                new GitBranch(BRANCH_NAME));

        gitTrackingAccess.addRepository(repoTwo);

        resultManager.importBenchmarkingResults(results);

        resultManager.deleteAllResultsForRepository(repoTwo.getId());

        CommitResult savedResultOne = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);
        CommitResult savedResultTwo = resultDB.getResultFromCommit(HASH_TWO);

        assertNotNull(savedResultOne);
        assertNull(savedResultTwo);
    }

    /**
     * Tests whether deleteBenchmarkingResults removes the result from the database.
     */
    @Test
    void deleteBenchmarkingResults_shouldRemoveFromDB() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        resultManager.deleteBenchmarkingResults(SimpleBenchmarkingResult.COMMIT_HASH);

        CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertNull(savedResult);
    }

    /**
     * Tests whether saveBenchmarkingResults can save a result for a commit with a parent that already has a result.
     */
    @Test
    void saveBenchmarkingResults_withParent_shouldSaveAsUsual() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        Set<GitCommit> parents = new HashSet<>();
        parents.add(commit);
        GitCommit commitTwo = new GitCommit(HASH_TWO, MSG, NOW, NOW, parents, repository, new GitBranch(BRANCH_NAME));

        gitTrackingAccess.addCommit(commitTwo);

        SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        resultManager.saveBenchmarkingResults(resultTwo);

        CommitResult savedResultTwo = resultDB.getResultFromCommit(HASH_TWO);

        assertNotNull(savedResultTwo);
        assertEquals(HASH_TWO, savedResultTwo.getCommitHash());
    }
}
