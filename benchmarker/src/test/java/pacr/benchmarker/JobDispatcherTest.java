package pacr.benchmarker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.BenchmarkingResult;
import pacr.benchmarker.services.JobDispatcher;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Pavel Zwerschke
 */
public class JobDispatcherTest {

    private static final String RUNNER_DIR = "/src/test/resources/pacr/benchmarker/services";
    private static final String RELATIVE_TEST_REPO_PATH = "repositories/pacr-test-repository";

    private JobDispatcher jobDispatcher;

    @BeforeEach
    public void setUp() {
        jobDispatcher = new JobDispatcher(RUNNER_DIR);
    }

    @Test
    public void testRunner() throws IOException, InterruptedException {
        BenchmarkingResult result = jobDispatcher.dispatchJob(RELATIVE_TEST_REPO_PATH);

        assertEquals(null, result.getGlobalError());
    }

    @Test
    public void runnerError() throws IOException, InterruptedException {
        jobDispatcher.dispatchJob("nopath");
    }

}
