package pacr.benchmarker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.BenchmarkingResult;
import pacr.benchmarker.services.JobDispatcher;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for JobDispatcher.
 *
 * @author Pavel Zwerschke
 */
public class JobDispatcherTest {

    private static final String RUNNER_DIR = "/src/test/resources/pacr/benchmarker/services/";
    private static final String RELATIVE_TEST_REPO_PATH = "repositories/pacr-test-repository";

    private JobDispatcher jobDispatcher;

    @BeforeEach
    public void setUp() {
        jobDispatcher = new JobDispatcher("test.bat", RUNNER_DIR);
    }

    // TODO: Pavel mach mal richtig!!
    @Test @Disabled
    public void testRunner() {
        BenchmarkingResult result = jobDispatcher.dispatchJob(RELATIVE_TEST_REPO_PATH);

        assertNull(result.getGlobalError());
    }

    @Test
    public void runnerError() {
        jobDispatcher.dispatchJob("nopath");
    }

}
