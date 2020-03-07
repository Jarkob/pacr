package pacr.benchmarker;

import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.SystemEnvironment;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SystemEnvironmentTest {

    @Test
    public void getSystemEnvironmentTest() {
        SystemEnvironment environment = SystemEnvironment.getInstance();

        assertNotNull(environment);
        assertNotNull(environment.getComputerName());
        assertNotNull(environment.getOs());
        assertNotNull(environment.getKernel());
        assertNotNull(environment.getProcessor());
        assertNotEquals(0, environment.getCores());
        assertNotEquals(0, environment.getRam());
    }

}
