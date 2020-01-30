package pacr.webapp_backend.import_export.servies;

import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents information about the system environment of a PACR-Benchmarker.
 */
public class SystemEnvironment implements ISystemEnvironment {

    private String computerName;
    private String os;
    private String kernel;
    private String processor;
    private int cores;

    // in Gigabytes
    private long ramMemory;

    /**
     * Creates an empty SystemEnvironment.
     *
     * Necessary for Spring to work.
     */
    public SystemEnvironment() {
    }

    /**
     * Creates a SystemEnvironment from an ISystemEnvironment interface.
     *
     * @param environment the ISystemEnvironment which is used to create the SystemEnvironment.
     */
    public SystemEnvironment(ISystemEnvironment environment) {
        this.cores = environment.getCores();
        this.computerName = environment.getComputerName();
        this.os = environment.getOS();
        this.kernel = environment.getKernel();
        this.processor = environment.getProcessor();
        this.ramMemory = environment.getRamMemory();
    }

    @Override
    public String getComputerName() {
        return computerName;
    }

    @Override
    public String getOS() {
        return os;
    }

    @Override
    public String getProcessor() {
        return processor;
    }

    @Override
    public String getKernel() {
        return kernel;
    }

    @Override
    public int getCores() {
        return cores;
    }

    @Override
    public long getRamMemory() {
        return ramMemory;
    }

}
