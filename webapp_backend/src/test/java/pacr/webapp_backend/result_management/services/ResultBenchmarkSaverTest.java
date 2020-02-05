package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.GitTrackingDB;
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
    private ResultGetter resultGetter;
    private ResultBenchmarkSaver resultBenchmarkSaver;
    private GitTrackingDB gitTrackingAccess;
    private EventHandler eventHandler;

    @Autowired
    public ResultBenchmarkSaverTest(ResultManager resultManager, ResultGetter resultGetter,
                                    ResultBenchmarkSaver resultBenchmarkSaver, GitTrackingDB gitTrackingDB,
                                    EventHandler eventHandler) {
        this.resultManager = resultManager;
        this.resultGetter = resultGetter;
        this.resultBenchmarkSaver = resultBenchmarkSaver;
        this.gitTrackingAccess = gitTrackingDB;
        this.eventHandler = eventHandler;
    }

    /**
     * Creates a repository and a commit.
     */
    @BeforeEach
    public void setUp() {
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

    /**
     * Tests whether the proper event was created when a result with comparison is saved.
     */
    @Test @Disabled
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

        assertEquals(EXPECTED_EVENT_DESCRIPTION, events.get(1).getDescription());
    }

}
