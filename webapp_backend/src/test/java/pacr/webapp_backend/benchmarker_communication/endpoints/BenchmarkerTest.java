package pacr.webapp_backend.benchmarker_communication.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pacr.webapp_backend.benchmarker_communication.services.SystemEnvironment;
import pacr.webapp_backend.shared.IJob;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BenchmarkerTest {

    private static final String ADDRESS = "address";

    private Benchmarker benchmarker;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Mock
    private IJob currentJob;

    @BeforeEach
    void setUp() {
        this.benchmarker = new Benchmarker(ADDRESS, systemEnvironment, currentJob);
    }

    @Test
    void Benchmarker_noArgs() {
        assertDoesNotThrow(() -> {
           Benchmarker benchmarker = new Benchmarker();
        });
    }

    @Test
    void Benchmarker_withArgs() {
        assertDoesNotThrow(() -> {
            Benchmarker benchmarker = new Benchmarker(ADDRESS, systemEnvironment, currentJob);
        });
    }

    @Test
    void getAddress_noError() {
        assertEquals(ADDRESS, benchmarker.getAddress());
    }

    @Test
    void getCurrentJob_noError() {
        assertEquals(currentJob, benchmarker.getCurrentJob());
    }

    @Test
    void getSystemEnvironment_noError() {
        assertEquals(systemEnvironment, benchmarker.getSystemEnvironment());
    }

    @Test
    void compareTo_noError() {
        Benchmarker otherBenchmarker = new Benchmarker(ADDRESS + 1, systemEnvironment, currentJob);

        int result = benchmarker.compareTo(otherBenchmarker);
        assertEquals(-1, result);

        result = otherBenchmarker.compareTo(benchmarker);
        assertEquals(1, result);

        result = benchmarker.compareTo(benchmarker);
        assertEquals(0, result);
    }
}
