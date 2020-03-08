package pacr.benchmarker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.BenchmarkProperty;
import pacr.benchmarker.services.BenchmarkingResult;
import pacr.benchmarker.services.JobDispatcher;
import pacr.benchmarker.services.ResultInterpretation;

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
    private static String runnerScript = "test";
    private static String genericError = "generic-error";
    private static String runnerScriptExtension;

    @BeforeAll
    public static void getOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            runnerScriptExtension = ".bat";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            runnerScriptExtension = ".sh";
        }
    }

    @BeforeEach
    public void setUp() {
        jobDispatcher = new JobDispatcher(runnerScript + runnerScriptExtension, RUNNER_DIR);
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
    public void genericError() {
        String script = genericError + runnerScriptExtension;

        jobDispatcher = new JobDispatcher(script, RUNNER_DIR);

        BenchmarkingResult result = jobDispatcher.dispatchJob(RELATIVE_TEST_REPO_PATH);

        assertTrue(result.getBenchmarks().containsKey("standard_lib"));
        BenchmarkProperty property = result.getBenchmarks().get("standard_lib").getProperties().get("time");
        assertEquals("generic error", property.getError());

        assertEquals(ResultInterpretation.NEUTRAL, property.getResultInterpretation());
        assertEquals("", property.getUnit());
    }

    @Test
    public void runnerError() {
        jobDispatcher.dispatchJob("nopath");
    }

}
