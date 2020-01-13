package pacr.webapp_backend.benchmarker_communication.services;

import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents information about the system environment of a PACR-Benchmarker.
 */
public class SystemEnvironment implements ISystemEnvironment {

    private String os;
    private String kernel;
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

    @Override
    public String getOS() {
        return os;
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
