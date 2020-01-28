package pacr.benchmarker.services;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class SystemEnvironment {

    private static SystemEnvironment instance;

    public static SystemEnvironment getInstance() {
        if (instance == null) {
            instance = new SystemEnvironment();
        }

        instance.updateParameters();

        return instance;
    }

    private String computerName;
    private String os;
    private String kernel;
    private String processor;
    private int cores;
    private long ramMemory;

    private SystemEnvironment() {
    }

    private void updateParameters() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();

        // todo name?
        ramMemory = hardwareAbstractionLayer.getMemory().getTotal() / (long) Math.pow(2, 30); // in GiB
        processor = hardwareAbstractionLayer.getProcessor().getProcessorIdentifier().getName();
        cores = hardwareAbstractionLayer.getProcessor().getLogicalProcessorCount();

        kernel = System.getenv("PROCESSOR_ARCHITECTURE");
        os = System.getProperty("os.name");
    }

    public String getOs() {
        return os;
    }

    public String getProcessor() {
        return processor;
    }

    public String getKernel() {
        return kernel;
    }

    public long getRamMemory() {
        return ramMemory;
    }

    public int getCores() {
        return cores;
    }
}
