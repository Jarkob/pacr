package pacr.webapp_backend.benchmarker_communication.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SystemEnvironmentTest {

    private SystemEnvironment systemEnvironment;

    @BeforeEach
    void setUp() {
        this.systemEnvironment = new SystemEnvironment();
    }

    @Test
    void gettersAvailable_returnDefaultValues() {
        assertNull(systemEnvironment.getComputerName());
        assertNull(systemEnvironment.getKernel());
        assertNull(systemEnvironment.getOs());
        assertNull(systemEnvironment.getProcessor());
        assertEquals(0, systemEnvironment.getCores());
        assertEquals(0, systemEnvironment.getRam());
    }
}
