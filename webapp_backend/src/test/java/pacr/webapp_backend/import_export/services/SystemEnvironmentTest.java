package pacr.webapp_backend.import_export.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.import_export.servies.SystemEnvironment;
import pacr.webapp_backend.shared.ISystemEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SystemEnvironmentTest {

    private static final String COMPUTER_NAME = "computerName";
    private static final String OS = "os";
    private static final String KERNEL = "kernel";
    private static final String PROCESSOR = "processor";
    private static final long RAM = 512;
    private static final int CORES = 8;

    private SystemEnvironment systemEnvironment;

    @Mock
    private ISystemEnvironment systemEnvironmentInterface;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(systemEnvironmentInterface.getComputerName()).thenReturn(COMPUTER_NAME);
        when(systemEnvironmentInterface.getCores()).thenReturn(CORES);
        when(systemEnvironmentInterface.getKernel()).thenReturn(KERNEL);
        when(systemEnvironmentInterface.getOs()).thenReturn(OS);
        when(systemEnvironmentInterface.getProcessor()).thenReturn(PROCESSOR);
        when(systemEnvironmentInterface.getRam()).thenReturn(RAM);

        this.systemEnvironment = new SystemEnvironment(systemEnvironmentInterface);
    }

    @Test
    void SystemEnvironment_noArgs() {
        assertDoesNotThrow(() -> {
            SystemEnvironment systemEnvironment = new SystemEnvironment();
        });
    }

    @Test
    void SystemEnvironment_withArgs() {
        assertDoesNotThrow(() -> {
            SystemEnvironment systemEnvironment = new SystemEnvironment(systemEnvironmentInterface);
        });
    }

    @Test
    void getComputerName_noError() {
        assertEquals(COMPUTER_NAME, systemEnvironment.getComputerName());
    }

    @Test
    void getKernel_noError() {
        assertEquals(KERNEL, systemEnvironment.getKernel());
    }

    @Test
    void getOs_noError() {
        assertEquals(OS, systemEnvironment.getOs());
    }

    @Test
    void getProcessor_noError() {
        assertEquals(PROCESSOR, systemEnvironment.getProcessor());
    }

    @Test
    void getCores_noError() {
        assertEquals(CORES, systemEnvironment.getCores());
    }

    @Test
    void getRam_noError() {
        assertEquals(RAM, systemEnvironment.getRam());
    }

}
