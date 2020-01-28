package pacr.webapp_backend.benchmarker_communication.services;

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
    private long ramMemory; // in GiB

    /**
     * Creates an empty SystemEnvironment.
     *
     * Necessary for Spring to work.
     */
    public SystemEnvironment() {
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
