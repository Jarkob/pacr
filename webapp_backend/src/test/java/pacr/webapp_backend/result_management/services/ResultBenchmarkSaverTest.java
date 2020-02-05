package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.BenchmarkGroupDB;
import pacr.webapp_backend.database.CommitDB;
import pacr.webapp_backend.database.EventDB;
import pacr.webapp_backend.database.GitTrackingDB;
import pacr.webapp_backend.database.RepositoryDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.EventHandler;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.EventCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultBenchmarkSaverTest extends SpringBootTestWithoutShell {

    private static final String HASH_TWO = "hash2";
    private static final String MSG = "msg";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String BRANCH_NAME = "branch";
    private static final String URL = "url";
    private static final String REPO_NAME = "repo";
    private static final String EXPECTED_EVENT_DESCRIPTION =
            "A new benchmarking result was measured for the commit 'hash2' from repository 'repo'. On average, " +
                    "the new benchmarking result is 50 percent better then the previous one (commit '1325').";

    private ResultManager resultManager;
    private GitTrackingDB gitTrackingAccess;
    private EventHandler eventHandler;
    private EventDB eventDB;
    private ResultDB resultDB;
    private CommitDB commitDB;
    private RepositoryDB repositoryDB;
    private BenchmarkGroupDB benchmarkGroupDB;
    private BenchmarkDB benchmarkDB;
    private BenchmarkManager benchmarkManager;

    @Autowired
    public ResultBenchmarkSaverTest(ResultManager resultManager, GitTrackingDB gitTrackingDB,
                                    EventHandler eventHandler, EventDB eventDB, ResultDB resultDB, CommitDB commitDB,
                                    RepositoryDB repositoryDB, BenchmarkGroupDB benchmarkGroupDB,
                                    BenchmarkDB benchmarkDB) {
        this.resultManager = resultManager;
        this.gitTrackingAccess = gitTrackingDB;
        this.eventHandler = eventHandler;
        this.eventDB = eventDB;
        this.resultDB = resultDB;
        this.commitDB = commitDB;
        this.repositoryDB = repositoryDB;
        this.benchmarkGroupDB = benchmarkGroupDB;
        this.benchmarkDB = benchmarkDB;
        this.benchmarkManager = new BenchmarkManager(benchmarkDB, benchmarkGroupDB);
    }

    /**
     * Creates a repository and a commit.
     */
    @BeforeEach
    public void setUp() {
        eventDB.deleteAll();

        // repository
        GitBranch branch = new GitBranch(BRANCH_NAME);
        GitRepository repository = new GitRepository(true, URL, REPO_NAME,
                "#000000", LocalDate.now());

        // commit
        GitCommit commit = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW, repository);

        GitCommit commitTwo = new GitCommit(HASH_TWO, MSG, NOW, NOW, repository);
        commitTwo.addParent(SimpleBenchmarkingResult.COMMIT_HASH);

        gitTrackingAccess.addRepository(repository);
        gitTrackingAccess.addCommit(commit);
        gitTrackingAccess.addCommit(commitTwo);
    }

    @AfterEach
    public void cleanUp() {
        eventDB.deleteAll();
        resultDB.deleteAll();
        commitDB.deleteAll();
        repositoryDB.deleteAll();
        benchmarkDB.deleteAll();
        benchmarkGroupDB.deleteAll();
        benchmarkManager = new BenchmarkManager(benchmarkDB, benchmarkGroupDB);
    }

    /**
     * Tests whether the proper event was created when a result with comparison is saved.
     */
    @Test
    public void updateOtherComponents_shouldCreateEvent() {
        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        List<Double> results = new LinkedList<>();
        results.add(18d);
        resultTwo.getBenchmark(SimpleBenchmarkingResult.BENCHMARK_NAME)
                .getProperty(SimpleBenchmark.PROPERTY_NAME).setResults(results);

        resultManager.saveBenchmarkingResults(resultTwo);

        List<Event> events = eventHandler.getEvents(EventCategory.BENCHMARKING);

        boolean foundEvent = false;

        for (Event event : events) {
            if (event.getDescription().equals(EXPECTED_EVENT_DESCRIPTION)) {
                foundEvent = true;
                break;
            }
        }

        assertTrue(foundEvent);
    }

}
