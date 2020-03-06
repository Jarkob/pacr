package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.IObserver;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ResultBenchmarkSaverTest extends SpringBootTestWithoutShell {

    private static final String HASH_TWO = "hash2";
    private static final String MSG = "msg";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String BRANCH_NAME = "branch";
    private static final String MASTER_BRANCH = "master";
    private static final String REPO_NAME = "repo";
    private static final String EXPECTED_EVENT_DESCRIPTION =
            "On average, the result is 50% better than the previous one ('1325').";

    private final ResultManager resultManager;
    private final GitTrackingDB gitTrackingAccess;
    private final EventHandler eventHandler;
    private final EventDB eventDB;
    private final ResultDB resultDB;
    private final CommitDB commitDB;
    private final RepositoryDB repositoryDB;
    private final BenchmarkGroupDB benchmarkGroupDB;
    private final BenchmarkDB benchmarkDB;
    private final ResultGetter resultGetter;
    private BenchmarkManager benchmarkManager;

    @Autowired
    public ResultBenchmarkSaverTest(final ResultManager resultManager, final GitTrackingDB gitTrackingDB,
                                    final EventHandler eventHandler, final EventDB eventDB, final ResultDB resultDB, final CommitDB commitDB,
                                    final RepositoryDB repositoryDB, final BenchmarkGroupDB benchmarkGroupDB,
                                    final BenchmarkDB benchmarkDB, final ResultGetter resultGetter) {
        this.resultManager = resultManager;
        this.gitTrackingAccess = gitTrackingDB;
        this.eventHandler = eventHandler;
        this.eventDB = eventDB;
        this.resultDB = resultDB;
        this.commitDB = commitDB;
        this.repositoryDB = repositoryDB;
        this.benchmarkGroupDB = benchmarkGroupDB;
        this.benchmarkDB = benchmarkDB;
        this.resultGetter = resultGetter;
        this.benchmarkManager = new BenchmarkManager(benchmarkDB, benchmarkGroupDB);
    }

    /**
     * Creates a repository and a commit.
     */
    @BeforeEach
    public void setUp() {
        eventDB.deleteAll();

        // repository
        final GitRepository repository = new GitRepository();
        repository.setName(REPO_NAME);
        repository.createBranchIfNotExists(MASTER_BRANCH);
        repository.createBranchIfNotExists(BRANCH_NAME);

        // commit
        final GitCommit commit = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH, MSG, NOW, NOW, repository);
        commit.addBranch(repository.getTrackedBranch(MASTER_BRANCH));

        final GitCommit commitTwo = new GitCommit(HASH_TWO, MSG, NOW, NOW, repository);
        commitTwo.addParent(SimpleBenchmarkingResult.COMMIT_HASH);
        commitTwo.addBranch(repository.getTrackedBranch(BRANCH_NAME));

        gitTrackingAccess.addRepository(repository);
        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(commit)));
        gitTrackingAccess.addCommits(new HashSet<>(Arrays.asList(commitTwo)));
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

        final SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        final List<Double> results = new LinkedList<>();
        results.add(18d);
        resultTwo.getBenchmark(SimpleBenchmarkingResult.BENCHMARK_NAME)
                .getProperty(SimpleBenchmark.PROPERTY_NAME).setResults(results);

        resultManager.saveBenchmarkingResults(resultTwo);

        final List<Event> events = eventHandler.getEvents(EventCategory.BENCHMARKING);

        boolean foundEvent = false;

        for (final Event event : events) {
            if (event.getDescription().equals(EXPECTED_EVENT_DESCRIPTION)) {
                foundEvent = true;
                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    void updateOtherComponents_commitOnBranchMaster_shouldUpdateObservers() {
        final IObserver observer = Mockito.mock(IObserver.class);
        resultGetter.subscribe(observer);

        resultManager.saveBenchmarkingResults(new SimpleBenchmarkingResult());

        verify(observer).update();
    }

    @Test
    void updateOtherComponents_commitNotOnBranchMaster_shouldNotUpdateObservers() {

        final IObserver observer = Mockito.mock(IObserver.class);
        resultGetter.subscribe(observer);

        final SimpleBenchmarkingResult resultTwo = new SimpleBenchmarkingResult();
        resultTwo.setCommitHash(HASH_TWO);

        resultManager.saveBenchmarkingResults(resultTwo);

        verify(observer, never()).update();
    }
}
