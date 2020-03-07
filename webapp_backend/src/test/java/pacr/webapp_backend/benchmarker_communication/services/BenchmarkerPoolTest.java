package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

public class BenchmarkerPoolTest {

    @Mock
    private JobHandler jobHandler;

    private BenchmarkerPool benchmarkerPool;

    private final String ADDRESS = "benchmarkerAddress";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        this.benchmarkerPool = new BenchmarkerPool();
        this.benchmarkerPool.addListener(jobHandler);
    }

    @Test
    void addListener_noError() {
        assertDoesNotThrow(() -> {
            benchmarkerPool.addListener(jobHandler);
        });

        assertDoesNotThrow(() -> {
            benchmarkerPool.addListener(null);
        });

        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        verify(jobHandler).newRegistration();
    }

    @Test
    void registerBenchmarker_noError() {
        boolean result = benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        assertTrue(result);
        assertTrue(benchmarkerPool.hasFreeBenchmarkers());
        verify(jobHandler).newRegistration();
    }

    @Test
    void registerBenchmarker_duplicate() {
        SystemEnvironment environment = new SystemEnvironment();
        benchmarkerPool.registerBenchmarker(ADDRESS, environment);

        boolean result = benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        Collection<String> addresses = benchmarkerPool.getAllBenchmarkerAddresses();

        assertFalse(result);

        assertEquals(1, addresses.size());
        assertTrue(addresses.contains(ADDRESS));
    }

    @Test
    void registerBenchmarker_nullAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.registerBenchmarker(null, new SystemEnvironment());
        });
    }

    @Test
    void registerBenchmarker_emptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.registerBenchmarker("", new SystemEnvironment());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.registerBenchmarker(" ", new SystemEnvironment());
        });
    }

    @Test
    void registerBenchmarker_nullSystemEnvironment() {
        assertThrows(NullPointerException.class, () -> {
            benchmarkerPool.registerBenchmarker(ADDRESS, null);
        });
    }

    @Test
    void unregisterBenchmarker_freeBenchmarker() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        boolean result = benchmarkerPool.unregisterBenchmarker(ADDRESS);

        assertTrue(result);
        assertFalse(benchmarkerPool.hasFreeBenchmarkers());

        Collection<String> allBenchmarkers = benchmarkerPool.getAllBenchmarkerAddresses();
        assertEquals(0, allBenchmarkers.size());
    }

    @Test
    void unregisterBenchmarker_occupiedBenchmarker() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());
        benchmarkerPool.occupyBenchmarker(ADDRESS);

        boolean result = benchmarkerPool.unregisterBenchmarker(ADDRESS);

        assertTrue(result);
        assertFalse(benchmarkerPool.hasFreeBenchmarkers());

        Collection<String> allBenchmarkers = benchmarkerPool.getAllBenchmarkerAddresses();
        assertEquals(0, allBenchmarkers.size());
    }

    @Test
    void unregisterBenchmarker_unknown() {
        boolean result = benchmarkerPool.unregisterBenchmarker(ADDRESS);

        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_nullAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.unregisterBenchmarker(null);
        });
    }

    @Test
    void unregisterBenchmarker_emptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.unregisterBenchmarker("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.unregisterBenchmarker(" ");
        });
    }

    @Test
    void hasFreeBenchmarkers_hasFree() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void hasFreeBenchmarkers_hasFreeAndOccupied() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());
        benchmarkerPool.registerBenchmarker(ADDRESS + "Other", new SystemEnvironment());

        benchmarkerPool.occupyBenchmarker(ADDRESS);

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void hasFreeBenchmarkers_isEmpty() {
        assertFalse(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void hasFreeBenchmarkers_allOccupied() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());
        benchmarkerPool.occupyBenchmarker(ADDRESS);

        assertFalse(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void getFreeBechmarker_hasFree() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        String address = benchmarkerPool.getFreeBenchmarker();

        assertEquals(ADDRESS, address);
    }

    @Test
    void getFreeBechmarker_hasFreeAndOccupied() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        final String SECOND_ADDRESS = ADDRESS + "Other";
        benchmarkerPool.registerBenchmarker(SECOND_ADDRESS, new SystemEnvironment());

        benchmarkerPool.occupyBenchmarker(SECOND_ADDRESS);

        String address = benchmarkerPool.getFreeBenchmarker();

        assertEquals(ADDRESS, address);
    }

    @Test
    void getFreeBechmarker_isEmpty() {
        assertNull(benchmarkerPool.getFreeBenchmarker());
    }

    @Test
    void getFreeBechmarker_allOccupied() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());
        benchmarkerPool.occupyBenchmarker(ADDRESS);

        assertNull(benchmarkerPool.getFreeBenchmarker());
    }

    @Test
    void getFreeBenchmarker_staysFree() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.getFreeBenchmarker();

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void freeBenchmarker_hasOccupied() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());
        benchmarkerPool.occupyBenchmarker(ADDRESS);

        benchmarkerPool.freeBenchmarker(ADDRESS);

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void freeBenchmarker_empty() {
        benchmarkerPool.freeBenchmarker(ADDRESS);

        assertFalse(benchmarkerPool.hasFreeBenchmarkers());

        Collection<String> addresses = benchmarkerPool.getAllBenchmarkerAddresses();
        assertEquals(0, addresses.size());
    }

    @Test
    void freeBenchmarker_noOccupied() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.freeBenchmarker(ADDRESS);

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());

        Collection<String> addresses = benchmarkerPool.getAllBenchmarkerAddresses();
        assertEquals(1, addresses.size());
    }

    @Test
    void freeBenchmarker_unknown() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.freeBenchmarker(ADDRESS + "wrongAddress");

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());

        Collection<String> addresses = benchmarkerPool.getAllBenchmarkerAddresses();
        assertEquals(1, addresses.size());
    }

    @Test
    void freeBenchmarker_nullAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.freeBenchmarker(null);
        });
    }

    @Test
    void freeBenchmarker_emptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.freeBenchmarker("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.freeBenchmarker(" ");
        });
    }

    @Test
    void occupyBenchmarker_hasFree() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.occupyBenchmarker(ADDRESS);

        assertFalse(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void occupyBenchmarker_empty() {
        benchmarkerPool.occupyBenchmarker(ADDRESS);

        assertFalse(benchmarkerPool.hasFreeBenchmarkers());

        Collection<String> addresses = benchmarkerPool.getAllBenchmarkerAddresses();
        assertEquals(0, addresses.size());
    }

    @Test
    void occupyBenchmarker_unknown() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.occupyBenchmarker(ADDRESS + "wrongAddress");

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());

        Collection<String> addresses = benchmarkerPool.getAllBenchmarkerAddresses();
        assertEquals(1, addresses.size());
    }

    @Test
    void occupyBenchmarker_nullAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.occupyBenchmarker(null);
        });
    }

    @Test
    void occupyBenchmarker_emptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.occupyBenchmarker("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.occupyBenchmarker(" ");
        });
    }

    @Test
    void getBenchmarkerSystemEnvironment_noError() {
        SystemEnvironment expectedSystemEnvironment = new SystemEnvironment();

        benchmarkerPool.registerBenchmarker(ADDRESS, expectedSystemEnvironment);

        SystemEnvironment systemEnvironment = benchmarkerPool.getBenchmarkerSystemEnvironment(ADDRESS);

        assertEquals(expectedSystemEnvironment, systemEnvironment);
    }

    @Test
    void getBenchmarkerSystemEnvironment_unknownAddress() {
        SystemEnvironment systemEnvironment = benchmarkerPool.getBenchmarkerSystemEnvironment(ADDRESS);

        assertNull(systemEnvironment);
    }

    @Test
    void getBenchmarkerSystemEnvironment_invalidAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.getBenchmarkerSystemEnvironment("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.getBenchmarkerSystemEnvironment(" ");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.getBenchmarkerSystemEnvironment(null);
        });
    }
}
