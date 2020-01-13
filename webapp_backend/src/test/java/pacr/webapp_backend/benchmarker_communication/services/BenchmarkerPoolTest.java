package pacr.webapp_backend.benchmarker_communication.services;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkerPoolTest {

    private BenchmarkerPool benchmarkerPool;

    private final String ADDRESS = "benchmarkerAddress";

    @BeforeEach
    void setUp() {
        this.benchmarkerPool = new BenchmarkerPool(new JobHandler());
    }

    @Test
    void registerBenchmarker_noError() {
        boolean result = benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        assertTrue(result);
        assertTrue(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void registerBenchmarker_duplicate() {
        SystemEnvironment environment = new SystemEnvironment();
        benchmarkerPool.registerBenchmarker(ADDRESS, environment);

        boolean result = benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        Collection<SystemEnvironment> systemEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();

        assertFalse(result);
        assertEquals(1, systemEnvironments.size());
        assertTrue(systemEnvironments.contains(environment));
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
        assertThrows(IllegalArgumentException.class, () -> {
            benchmarkerPool.registerBenchmarker(ADDRESS, null);
        });
    }

    @Test
    void unregisterBenchmarker_noError() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        boolean result = benchmarkerPool.unregisterBenchmarker(ADDRESS);

        assertTrue(result);
        assertFalse(benchmarkerPool.hasFreeBenchmarkers());
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

        Collection<SystemEnvironment> systemEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();
        assertEquals(0, systemEnvironments.size());
    }

    @Test
    void freeBenchmarker_noOccupied() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.freeBenchmarker(ADDRESS);

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());

        Collection<SystemEnvironment> systemEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();
        assertEquals(1, systemEnvironments.size());
    }

    @Test
    void freeBenchmarker_unknown() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.freeBenchmarker(ADDRESS + "wrongAddress");

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());

        Collection<SystemEnvironment> systemEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();
        assertEquals(1, systemEnvironments.size());
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

        Collection<SystemEnvironment> systemEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();
        assertEquals(0, systemEnvironments.size());
    }

    @Test
    void occupyBenchmarker_unknown() {
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        benchmarkerPool.occupyBenchmarker(ADDRESS + "wrongAddress");

        assertTrue(benchmarkerPool.hasFreeBenchmarkers());

        Collection<SystemEnvironment> systemEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();
        assertEquals(1, systemEnvironments.size());
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
    void getBenchmarkerSystemEnvironments_onlyFree() {
        ArrayList<SystemEnvironment> environments = new ArrayList<>();

        int amtEnvironments = 5;
        for (int i = 0; i < amtEnvironments; i++) {
            environments.add(new SystemEnvironment());
        }

        for (int i = 0; i < amtEnvironments; i++) {
            benchmarkerPool.registerBenchmarker(ADDRESS + i, environments.get(i));
        }

        Collection<SystemEnvironment> benchmarkerEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();

        assertEquals(amtEnvironments, benchmarkerEnvironments.size());

        for (SystemEnvironment environment : benchmarkerEnvironments) {
            assertTrue(environments.contains(environment));
            environments.remove(environment);
        }
    }

    @Test
    void getBenchmarkerSystemEnvironments_onlyOccupied() {
        ArrayList<SystemEnvironment> environments = new ArrayList<>();

        int amtEnvironments = 5;
        for (int i = 0; i < amtEnvironments; i++) {
            environments.add(new SystemEnvironment());
        }

        for (int i = 0; i < amtEnvironments; i++) {
            benchmarkerPool.registerBenchmarker(ADDRESS + i, environments.get(i));
            benchmarkerPool.occupyBenchmarker(ADDRESS + i);
        }

        Collection<SystemEnvironment> benchmarkerEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();

        assertEquals(amtEnvironments, benchmarkerEnvironments.size());

        for (SystemEnvironment environment : benchmarkerEnvironments) {
            assertTrue(environments.contains(environment));
            environments.remove(environment);
        }
    }

    @Test
    void getBenchmarkerSystemEnvironments_freeAndOccupied() {
        ArrayList<SystemEnvironment> environments = new ArrayList<>();

        int amtEnvironmentsFree = 5;
        for (int i = 0; i < amtEnvironmentsFree; i++) {
            environments.add(new SystemEnvironment());
        }

        int amtEnvironmentsOccupied = 5;
        for (int i = 0; i < amtEnvironmentsOccupied; i++) {
            environments.add(new SystemEnvironment());
        }

        for (int i = 0; i < amtEnvironmentsFree; i++) {
            benchmarkerPool.registerBenchmarker(ADDRESS + i, environments.get(i));
        }

        for (int i = 0; i < amtEnvironmentsOccupied; i++) {
            int addressSuffix = amtEnvironmentsFree + i;
            benchmarkerPool.registerBenchmarker(ADDRESS + addressSuffix, environments.get(addressSuffix));
            benchmarkerPool.occupyBenchmarker(ADDRESS + addressSuffix);
        }

        Collection<SystemEnvironment> benchmarkerEnvironments = benchmarkerPool.getBenchmarkerSystemEnvironment();

        assertEquals(amtEnvironmentsFree + amtEnvironmentsOccupied, benchmarkerEnvironments.size());

        for (SystemEnvironment environment : benchmarkerEnvironments) {
            assertTrue(environments.contains(environment));
            environments.remove(environment);
        }
    }
}
