package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pacr.webapp_backend.result_management.services.SimpleBenchmark.PROPERTY_NAME;
import static pacr.webapp_backend.result_management.services.SimpleBenchmarkProperty.UNIT;
import static pacr.webapp_backend.result_management.services.SimpleBenchmarkingResult.BENCHMARK_NAME;


public class OutputResultTest {

    private static final int REPO_ID = 1;
    private static final int BENCHMARK_ID = 1;
    private static final int DIFFERENT_BENCHMARK_ID = 2895589;
    private static final String HASH_TWO = "differentHash";
    private static final String MSG = "msg";
    private static final String ERROR_MESSAGE = "error";

    @Test
    void constructor_differentHashes_shouldThrowException() {
        final CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), REPO_ID, LocalDateTime.now(), null);
        final ICommit commit = new GitCommit(HASH_TWO, MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        assertThrows(IllegalArgumentException.class, () -> new DiagramOutputResult(result, commit, BENCHMARK_ID));
    }

    @Test
    void constructor_noResultForBenchmark_shouldReturnObjectWithEmptyResultMap() {
        CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), REPO_ID, LocalDateTime.now(), null);
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        BenchmarkResult benchmarkResult = new BenchmarkResult(benchmark);
        result.addBenchmarkResult(benchmarkResult);

        ICommit commit = new GitCommit(SimpleBenchmarkingResult.COMMIT_HASH,
                MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        DiagramOutputResult output = new DiagramOutputResult(result, commit, DIFFERENT_BENCHMARK_ID);

        assertTrue(output.getResult().isEmpty());
    }

    @Test
    void constructorDetail_differentHashes_shouldThrowException() {
        final CommitResult result = new CommitResult(new SimpleBenchmarkingResult(), REPO_ID, LocalDateTime.now(), null);
        final ICommit commit = new GitCommit(HASH_TWO, MSG, LocalDateTime.now(), LocalDateTime.now(), new GitRepository());

        assertThrows(IllegalArgumentException.class,
                () -> new OutputBenchmarkingResult(commit, result, new OutputBenchmark[0]));
    }

    @Test
    void resultWithErrorConstructor_propertyWithErrorAsParameter_shouldCopyErrorMessageAndHaveNullResult() {
        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult(new SimpleBenchmarkProperty(), property);
        propertyResult.setError(true);
        propertyResult.setErrorMessage(ERROR_MESSAGE);

        ResultWithError resultWithError = new ResultWithError(propertyResult);

        assertNull(resultWithError.getResult());
        assertEquals(ERROR_MESSAGE, resultWithError.getErrorMessage());
    }
}
