package pacr.webapp_backend.result_management.services;

import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.BenchmarkGroupDB;
import pacr.webapp_backend.database.RepositoryDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.shared.IBenchmarkingResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pacr.webapp_backend.result_management.services.SimpleCommit.REPO_ID;

public class ResultManagerTest extends SpringBootTestWithoutShell {

    private static final String HASH_TWO = "hash2";
    private static final String MSG = "msg";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String REPO_NAME = "repo";

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
    public ResultManagerTest(ResultManager resultManager, ResultDB resultDB, IGitTrackingAccess gitTrackingAccess,
                             RepositoryDB repositoryDB, BenchmarkDB benchmarkDB, BenchmarkGroupDB groupDB) {
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
    public void cleanUp() throws NotFoundException {
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

        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(new GitCommit(HASH_TWO, MSG, NOW, NOW, repository))));

        resultManager.importBenchmarkingResults(results);

        CommitResult savedResultOne = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);
        CommitResult savedResultTwo = resultDB.getResultFromCommit(HASH_TWO);

        assertNotNull(savedResultOne);
        assertEquals(SimpleBenchmarkingResult.COMMIT_HASH, savedResultOne.getCommitHash());

        assertNotNull(savedResultTwo);
        assertEquals(HASH_TWO, savedResultTwo.getCommitHash());
    }

    @Test
    void importBenchmarkingResults_noCommitSaved_shouldThrowException() {
        SimpleBenchmarkingResult resultToImport = new SimpleBenchmarkingResult();
        resultToImport.setCommitHash(HASH_TWO);

        Collection<IBenchmarkingResult> resultsToImport = new LinkedList<>();
        resultsToImport.add(resultToImport);

        assertThrows(IllegalArgumentException.class, () -> resultManager.importBenchmarkingResults(resultsToImport));
    }

    /**
     * Tests whether deleteBenchmarkingResults removes the result from the database.
     */
    @Test
    void deleteBenchmarkingResults_shouldRemoveFromDB() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        resultManager.deleteBenchmarkingResults(Arrays.asList(SimpleBenchmarkingResult.COMMIT_HASH));

        CommitResult savedResult = resultDB.getResultFromCommit(SimpleBenchmarkingResult.COMMIT_HASH);

        assertNull(savedResult);
    }

    /**
     * Tests whether saveBenchmarkingResults can save a result for a commit with a parent that already has a result.
     */
    @Test
    void saveBenchmarkingResults_withParent_shouldSaveAsUsual() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        GitCommit commitTwo = new GitCommit(HASH_TWO, MSG, NOW, NOW, repository);
        commitTwo.addParent(SimpleBenchmarkingResult.COMMIT_HASH);

        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(commitTwo)));

        SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        resultManager.saveBenchmarkingResults(resultTwo);

        CommitResult savedResultTwo = resultDB.getResultFromCommit(HASH_TWO);

        assertNotNull(savedResultTwo);
        assertEquals(HASH_TWO, savedResultTwo.getCommitHash());
    }

    @Test
    void saveBenchmarkingResults_noCommitSavedForResult_shouldThrowException() {
        SimpleBenchmarkingResult resultToSave = new SimpleBenchmarkingResult();
        resultToSave.setCommitHash(HASH_TWO);

        assertThrows(IllegalArgumentException.class, () -> resultManager.saveBenchmarkingResults(resultToSave));
    }
}
