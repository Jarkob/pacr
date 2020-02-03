package pacr.benchmarker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.BenchmarkingResult;
import pacr.benchmarker.services.JobDispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for JobDispatcher.
 *
 * @author Pavel Zwerschke
 */
public class JobDispatcherTest {

    private static final String RUNNER_DIR = "/src/test/resources/pacr/benchmarker/services/";
    private static final String RELATIVE_TEST_REPO_PATH = "repositories/pacr-test-repository";

    private JobDispatcher jobDispatcher;
    private static String runnerScript;

    @BeforeAll
    public static void getOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            runnerScript = "test.bat";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            runnerScript = "test.sh";
        }
    }

    @BeforeEach
    public void setUp() {
        jobDispatcher = new JobDispatcher(runnerScript, RUNNER_DIR);
    }

    @Test
    public void testRunner() {
        BenchmarkingResult result = jobDispatcher.dispatchJob(RELATIVE_TEST_REPO_PATH);

        assertEquals("", result.getGlobalError());
        assertEquals(2, result.getBenchmarks().size());
        assertTrue(result.getBenchmarks().containsKey("TheBenchmark"));
        assertTrue(result.getBenchmarks().containsKey("TheOtherBenchmark"));
    }

    @Test
    public void runnerError() {
        jobDispatcher.dispatchJob("nopath");
    }

}
