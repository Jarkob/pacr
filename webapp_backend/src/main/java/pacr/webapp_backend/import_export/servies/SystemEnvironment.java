package pacr.webapp_backend.import_export.servies;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents information about the system environment of a PACR-Benchmarker.
 */
@NoArgsConstructor
@Getter
public class SystemEnvironment implements ISystemEnvironment {

    private String computerName;
    private String os;
    private String kernel;
    private String processor;
    private int cores;

    // in Gigabytes
    private long ram;

    /**
     * Creates a SystemEnvironment from an ISystemEnvironment interface.
     *
     * @param environment the ISystemEnvironment which is used to create the SystemEnvironment.
     */
    public SystemEnvironment(final ISystemEnvironment environment) {
        this.cores = environment.getCores();
        this.computerName = environment.getComputerName();
        this.os = environment.getOs();
        this.kernel = environment.getKernel();
        this.processor = environment.getProcessor();
        this.ram = environment.getRam();
    }

}
