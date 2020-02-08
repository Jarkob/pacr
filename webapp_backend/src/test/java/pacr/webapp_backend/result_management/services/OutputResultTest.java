package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.ICommit;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class OutputResultTest {

    private static final int REPO_ID = 1;
    private static final String HASH_TWO = "differentHash";
    private static final String MSG = "msg";

    @Test
    void constructor_differentHashes_shouldThrowException() {
        CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), new HashSet<>(), REPO_ID, LocalDateTime.now(), null);
        ICommit commit = new GitCommit(HASH_TWO, MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        assertThrows(IllegalArgumentException.class, () -> new DiagramOutputResult(result, commit));
    }

    @Test
    void constructorDetail_differentHashes_shouldThrowException() {
        CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), new HashSet<>(), REPO_ID, LocalDateTime.now(), null);
        ICommit commit = new GitCommit(HASH_TWO, MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        assertThrows(IllegalArgumentException.class,
                () -> new OutputBenchmarkingResult(commit, result, new OutputBenchmark[0]));
    }
}
