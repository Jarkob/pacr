package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.ICommit;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OutputResultTest {

    private static final int REPO_ID = 1;
    private static final int BENCHMARK_ID = 1;
    private static final int DIFFERENT_BENCHMARK_ID = 2895589;
    private static final String HASH_TWO = "differentHash";
    private static final String MSG = "msg";

    @Test
    void constructor_differentHashes_shouldThrowException() {
        CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), REPO_ID, LocalDateTime.now(), null);
        ICommit commit = new GitCommit(HASH_TWO, MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        assertThrows(IllegalArgumentException.class, () -> new DiagramOutputResult(result, commit, BENCHMARK_ID));
    }

    @Test
    void constructor_noResultForBenchmark_shouldReturnObjectWithEmptyResultMap() {
        CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), REPO_ID, LocalDateTime.now(), null);
        ICommit commit = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH,
                MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        DiagramOutputResult output = new DiagramOutputResult(result, commit, DIFFERENT_BENCHMARK_ID);

        assertTrue(output.getResult().isEmpty());
    }

    @Test
    void constructorDetail_differentHashes_shouldThrowException() {
        CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), REPO_ID, LocalDateTime.now(), null);
        ICommit commit = new GitCommit(HASH_TWO, MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        assertThrows(IllegalArgumentException.class,
                () -> new OutputBenchmarkingResult(commit, result, new OutputBenchmark[0]));
    }
}
