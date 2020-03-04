package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.BenchmarkGroupDB;
import pacr.webapp_backend.database.RepositoryDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.shared.IBenchmarkingResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultManagerTest extends SpringBootTestWithoutShell {

    private static final String HASH_TWO = "hash2";
    private static final String MSG = "msg";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String REPO_NAME = "repo";
    private static final int EXPECTED_TWO_RESULTS = 2;

    private ResultManager resultManager;
    private ResultDB resultDB;
    private IGitTrackingAccess gitTrackingAccess;
    private RepositoryDB repositoryDB;
    private BenchmarkDB benchmarkDB;
    private BenchmarkGroupDB groupDB;
    private BenchmarkManager benchmarkManager;

    private GitRepository repository;
    private GitCommit commit;

    @Autowired
    public ResultManagerTest(final ResultManager resultManager, final ResultDB resultDB, final IGitTrackingAccess gitTrackingAccess,
                             final RepositoryDB repositoryDB, final BenchmarkDB benchmarkDB, final BenchmarkGroupDB groupDB) {
        this.resultManager = resultManager;
        this.resultDB = resultDB;
        this.gitTrackingAccess = gitTrackingAccess;
        this.repositoryDB = repositoryDB;
        this.benchmarkDB = benchmarkDB;
        this.groupDB = groupDB;
        benchmarkManager = new BenchmarkManager(benchmarkDB, groupDB);
    }


    /**
     * Creates a repository and a commit.
     */
    @BeforeEach
    public void setUp() {
        // repository
        repository = new GitRepository();
        repository.setName(REPO_NAME);

        // commit
        commit = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW, repository);

        gitTrackingAccess.addRepository(repository);
        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(commit)));
    }

    @AfterEach
    public void cleanUp() {
        gitTrackingAccess.removeRepository(repository.getId());
        repositoryDB.deleteAll();
        resultDB.deleteAll();
        benchmarkDB.deleteAll();
        groupDB.deleteAll();
        benchmarkManager = new BenchmarkManager(benchmarkDB, groupDB);
    }

    /**
     * Tests whether a result saved with saveBenchmarkingResults can be found in the database.
     */
    @Test
    void saveBenchmarkingResults_shouldBeSavedInDatabase() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        final CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertNotNull(savedResult);
        assertEquals(SimpleBenchmarkingResult.COMMIT_HASH, savedResult.getCommitHash());
    }

    /**
     * Tests whether multiple results can be saved with importBenchmarkingResults.
     */
    @Test
    void importBenchmarkingResults_shouldBeSavedInDatabase() {
        final SimpleBenchmarkingResult resultOne = new SimpleBenchmarkingResult();
        final SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        final Collection<IBenchmarkingResult> results = new LinkedList<>();
        results.add(resultOne);
        results.add(resultTwo);

        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(new GitCommit(HASH_TWO, MSG, NOW, NOW, repository))));

        resultManager.importBenchmarkingResults(results);

        final CommitResult savedResultOne = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);
        final CommitResult savedResultTwo = resultDB.getResultFromCommit(HASH_TWO);

        assertNotNull(savedResultOne);
        assertEquals(SimpleBenchmarkingResult.COMMIT_HASH, savedResultOne.getCommitHash());

        assertNotNull(savedResultTwo);
        assertEquals(HASH_TWO, savedResultTwo.getCommitHash());
    }

    @Test
    void importBenchmarkingResults_noCommitSaved_shouldNotSaveResult() {
        SimpleBenchmarkingResult resultToImport = new SimpleBenchmarkingResult();
        resultToImport.setCommitHash(HASH_TWO);

        final Collection<IBenchmarkingResult> resultsToImport = new LinkedList<>();
        resultsToImport.add(resultToImport);

        resultManager.importBenchmarkingResults(resultsToImport);

        assertNull(resultDB.getResultFromCommit(HASH_TWO));
    }

    /**
     * Tests whether deleteBenchmarkingResults removes the result from the database.
     */
    @Test
    void deleteBenchmarkingResults_shouldRemoveFromDB() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        resultManager.deleteBenchmarkingResults(Arrays.asList(SimpleBenchmarkingResult.COMMIT_HASH));

        final CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertNull(savedResult);
    }

    /**
     * Tests whether saveBenchmarkingResults can save a result for a commit with a parent that already has a result.
     */
    @Test
    void saveBenchmarkingResults_withParent_shouldSaveAsUsual() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        final GitCommit commitTwo = new GitCommit(HASH_TWO, MSG, NOW, NOW, repository);
        commitTwo.addParent(SimpleBenchmarkingResult.COMMIT_HASH);

        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(commitTwo)));

        final SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        resultManager.saveBenchmarkingResults(resultTwo);

        final CommitResult savedResultTwo = resultDB.getResultFromCommit(HASH_TWO);

        assertNotNull(savedResultTwo);
        assertEquals(HASH_TWO, savedResultTwo.getCommitHash());
    }

    @Test
    void saveBenchmarkingResults_noCommitSavedForResult_shouldNotSaveResult() {
        SimpleBenchmarkingResult resultToSave = new SimpleBenchmarkingResult();
        resultToSave.setCommitHash(HASH_TWO);

        resultManager.saveBenchmarkingResults(resultToSave);

        assertNull(resultDB.getResultFromCommit(HASH_TWO));
    }

    /**
     * A comparison cannot be executed if the comparison commit has no saved result yet. However, as soon as that result
     * is saved, the results with that comparison commit hash should get updated with a comparison.
     */
    @Test
    void saveBenchmarkingResults_comparisonResultSavedAfterChild_shouldUpdateComparisonOfChild() {
        final GitCommit commitToCompare = new GitCommit(HASH_TWO, MSG, NOW, NOW, repository);
        commitToCompare.addParent(SimpleBenchmarkingResult.COMMIT_HASH);
        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(commitToCompare)));

        final SimpleBenchmarkingResult resultToCompare = new SimpleBenchmarkingResult();
        resultToCompare.setCommitHash(HASH_TWO);

        resultManager.saveBenchmarkingResults(resultToCompare);

        final CommitResult resultBeforeComparison = resultDB.getResultFromCommit(HASH_TWO);

        assertEquals(SimpleBenchmarkingResult.COMMIT_HASH, resultBeforeComparison.getComparisonCommitHash());
        assertFalse(resultBeforeComparison.isCompared());

        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        final CommitResult resultAfterComparison = resultDB.getResultFromCommit(HASH_TWO);

        assertEquals(SimpleBenchmarkingResult.COMMIT_HASH, resultAfterComparison.getComparisonCommitHash());
        assertTrue(resultAfterComparison.isCompared());

        final Collection<CommitResult> allResults = resultDB.getAllResults();
        assertEquals(EXPECTED_TWO_RESULTS, allResults.size());
    }
}
