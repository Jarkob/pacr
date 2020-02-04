package pacr.webapp_backend.database;

import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.result_management.services.SystemEnvironment;
import pacr.webapp_backend.result_management.services.Benchmark;
import pacr.webapp_backend.result_management.services.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.services.BenchmarkResult;
import pacr.webapp_backend.result_management.services.CommitResult;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultDBTest extends SpringBootTestWithoutShell {

    @Mock
    private SystemEnvironment systemEnvironmentMock;

    private static final String BENCHMARK_NAME = "benchmark";
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String GROUP_NAME = "group";
    private static final String COMMIT_HASH = "1111";
    private static final String COMMIT_HASH_TWO = "2222";
    private static final String COMMIT_HASH_THREE = "3333";
    private static final int REPO_ID_ONE = 1;
    private static final int REPO_ID_TWO = 2;
    private static final int EXPECTED_NUM_OF_RESULTS_ONE_NOT_SAVED = 1;

    private ResultDB resultDB;
    private BenchmarkDB benchmarkDB;
    private Benchmark benchmark;
    private Benchmark benchmarkTwo;

    @Autowired
    public ResultDBTest(ResultDB resultDB, BenchmarkDB benchmarkDB) {
        this.resultDB = resultDB;
        this.benchmarkDB = benchmarkDB;
        this.benchmark = new Benchmark(BENCHMARK_NAME);
        this.benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);
    }

    @BeforeEach
    public void setUp() {
        this.benchmarkDB.saveBenchmark(benchmark);
        this.benchmarkDB.saveBenchmark(benchmarkTwo);
    }

    @AfterEach
    public void cleanUp() {
        resultDB.deleteAll();
        benchmarkDB.deleteAll();
    }

    /**
     * Tests whether a result can be saved with saveResult and retrieved with getResultFromCommit
     */
    @Test
    public void saveResult_saveInDatabase_getResultShouldReturnResult() {
        CommitResult result = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(result);

        CommitResult savedResult = this.resultDB.getResultFromCommit(COMMIT_HASH);

        assertEquals(COMMIT_HASH, savedResult.getCommitHash());
        assertEquals(BENCHMARK_NAME, savedResult.getBenchmarksIterable().iterator().next().getName());
    }

    /**
     * Tests whether saving a result with a commit hash that already has a result properly replaces the result.
     */
    @Test
    public void saveResult_alreadySavedForCommit_shouldReplaceResult() {
        CommitResult result = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(result);

        CommitResult newResultForSameCommit = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_TWO);
        this.resultDB.saveResult(newResultForSameCommit);

        CommitResult savedResult = this.resultDB.getResultFromCommit(COMMIT_HASH);
        assertEquals(REPO_ID_TWO, savedResult.getRepositoryId());
    }

    /**
     * Tests whether getAllResults returns all saved results.
     */
    @Test
    public void getAllResults_multipleResultsSaved_shouldReturnAllResults() {
        CommitResult resultOne = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        CommitResult resultTwo = createNewCommitResult(COMMIT_HASH_TWO, benchmark, REPO_ID_TWO);

        this.resultDB.saveResult(resultOne);
        this.resultDB.saveResult(resultTwo);

        List<CommitResult> allSavedResults = this.resultDB.getAllResults();

        assertEquals(2, allSavedResults.size());

        boolean foundResultOne = false;
        boolean foundResultTwo = false;

        for (CommitResult savedResult : allSavedResults) {
            if (savedResult.getCommitHash().equals(COMMIT_HASH)) {
                foundResultOne = true;
            } else if (savedResult.getCommitHash().equals(COMMIT_HASH_TWO)) {
                foundResultTwo = true;
            }
        }

        assertTrue(foundResultOne && foundResultTwo);
    }

    /**
     * Tests whether the benchmark metadata of a result changes if the benchmark is changed in the benchmarkDB.
     */
    @Test
    public void getResult_changedBenchmark_ShouldReturnChangedBenchmark() {
        CommitResult result = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(result);

        benchmark.setCustomName(BENCHMARK_NAME_TWO);
        this.benchmarkDB.saveBenchmark(benchmark);

        CommitResult savedResult = this.resultDB.getResultFromCommit(COMMIT_HASH);

        assertEquals(BENCHMARK_NAME_TWO,
                savedResult.getBenchmarksIterable().iterator().next().getBenchmark().getCustomName());
    }

    /**
     * Tests whether the proper results are returned if you enter multiple commit hashes.
     */
    @Test
    public void getResultsFromCommits_multipleHashesAsInput_ShouldReturnAllResults() {
        CommitResult resultOne = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(resultOne);
        CommitResult resultTwo = createNewCommitResult(COMMIT_HASH_TWO, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(resultTwo);

        List<String> commitHashes = new LinkedList<>();
        commitHashes.add(COMMIT_HASH);
        commitHashes.add(COMMIT_HASH_TWO);

        Collection<CommitResult> savedResults = this.resultDB.getResultsFromCommits(commitHashes);

        assertEquals(2, savedResults.size());
    }

    /**
     * Tests whether getResultsFromCommits can handle commit hashes that have no result saved.
     */
    @Test
    public void getResultsFromCommits_oneHashHasResultOtherDoesNot_shouldReturnSavedResult() {
        CommitResult resultOne = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(resultOne);

        List<String> commitHashes = new LinkedList<>();
        commitHashes.add(COMMIT_HASH);
        commitHashes.add(COMMIT_HASH_TWO);

        Collection<CommitResult> savedResults = this.resultDB.getResultsFromCommits(commitHashes);

        assertEquals(EXPECTED_NUM_OF_RESULTS_ONE_NOT_SAVED, savedResults.size());
    }

    /**
     * Tests whether a result can be deleted.
     */
    @Test
    public void deleteResult_resultSaved_shouldRemoveResult() {
        CommitResult result = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(result);

        this.resultDB.delete(result);
        CommitResult deletedResult = this.resultDB.getResultFromCommit(COMMIT_HASH);

        assertNull(deletedResult);
    }

    /**
     * Tests whether multiple saved results are returned in the correct order by getNewestResult (even if a result is
     * deleted in between)
     * @throws InterruptedException if sleep fails.
     */
    @Test
    public void getNewestResults_multipleResultsSavedOneDeleted_shouldReturnOrdered() throws InterruptedException {
        this.resultDB.saveResult(createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE));

        CommitResult resultToDelete = createNewCommitResult(COMMIT_HASH_TWO, benchmark, REPO_ID_ONE);
        this.resultDB.saveResult(resultToDelete);

        TimeUnit.SECONDS.sleep(1);

        this.resultDB.saveResult(createNewCommitResult(COMMIT_HASH_THREE, benchmark, REPO_ID_ONE));

        this.resultDB.saveResult(createNewCommitResult(COMMIT_HASH_THREE, benchmark, REPO_ID_ONE));

        this.resultDB.delete(resultToDelete);

        TimeUnit.SECONDS.sleep(1);

        this.resultDB.saveResult(createNewCommitResult(COMMIT_HASH_TWO, benchmark, REPO_ID_ONE));

        TimeUnit.SECONDS.sleep(1);

        LocalDateTime previousTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<CommitResult> orderedResults = this.resultDB.getNewestResults();

        int i = 0;

        for (CommitResult result : orderedResults) {
            LocalDateTime currentTime = result.getEntryDate();
            assertTrue(currentTime.isBefore(previousTime) || currentTime.equals(previousTime),
                    "result number " + i + ": " + result.getCommitHash() + ": " + currentTime.toString()
                            + " is not before " + previousTime.toString());
            previousTime = result.getEntryDate();
            i++;
        }
    }

    /**
     * Tests whether getNewestResult returns the newest result for a repository and not of a different repository.
     * @throws InterruptedException if sleep action fails.
     */
    @Test
    void getNewestResult_multipleResultsForRepository_shouldReturnNewest() throws InterruptedException {
        CommitResult commitResultOne = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        resultDB.saveResult(commitResultOne);

        TimeUnit.SECONDS.sleep(1);

        CommitResult commitResultTwo = createNewCommitResult(COMMIT_HASH_TWO, benchmark, REPO_ID_ONE);
        resultDB.saveResult(commitResultTwo);

        TimeUnit.SECONDS.sleep(1);

        CommitResult commitResultThree = createNewCommitResult(COMMIT_HASH_THREE, benchmark, REPO_ID_TWO);
        resultDB.saveResult(commitResultThree);

        CommitResult newestResult = this.resultDB.getNewestResult(REPO_ID_ONE);

        assertNotNull(newestResult);
        assertThat(commitResultTwo.getEntryDate()).isCloseTo(newestResult.getEntryDate(), within(500, ChronoUnit.MILLIS));
        assertEquals(commitResultTwo.getCommitHash(), newestResult.getCommitHash());

        newestResult = this.resultDB.getNewestResult(REPO_ID_TWO);

        assertNotNull(newestResult);
        assertThat(commitResultThree.getEntryDate()).isCloseTo(newestResult.getEntryDate(), within(500, ChronoUnit.MILLIS));
        assertEquals(commitResultThree.getCommitHash(), newestResult.getCommitHash());
    }

    /**
     * Tests whether deleteAllByCommitHash only deletes the results with a commit hash in the given collection
     */
    @Test
    void deleteAllByCommitHash_deletesCorrectResults() {
        CommitResult commitResultOne = createNewCommitResult(COMMIT_HASH, benchmark, REPO_ID_ONE);
        resultDB.saveResult(commitResultOne);

        CommitResult commitResultTwo = createNewCommitResult(COMMIT_HASH_TWO, benchmark, REPO_ID_ONE);
        resultDB.saveResult(commitResultTwo);

        CommitResult commitResultThree = createNewCommitResult(COMMIT_HASH_THREE, benchmark, REPO_ID_TWO);
        resultDB.saveResult(commitResultThree);

        Collection<String> hashes = new HashSet<>();
        hashes.add(COMMIT_HASH);
        hashes.add(COMMIT_HASH_THREE);

        resultDB.deleteResults(hashes);

        hashes.add(COMMIT_HASH_TWO);

        Collection<CommitResult> savedResults = resultDB.getResultsFromCommits(hashes);

        assertEquals(1, savedResults.size());
        assertEquals(COMMIT_HASH_TWO, savedResults.iterator().next().getCommitHash());
    }

    private CommitResult createNewCommitResult(String commitHash, Benchmark benchmark, int repositoryId) {
        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult();

        Set<BenchmarkPropertyResult> propertyResults = new HashSet<>();
        propertyResults.add(propertyResult);
        BenchmarkResult benchmarkResult = new BenchmarkResult(propertyResults, benchmark);

        Set<BenchmarkResult> benchmarkResults = new HashSet<>();
        benchmarkResults.add(benchmarkResult);

        return new CommitResult(commitHash, systemEnvironmentMock, benchmarkResults, repositoryId);
    }
}
