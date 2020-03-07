package pacr.benchmarker.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Represents the system environment of the benchmarker.
 */
public class SystemEnvironment {

    private static final Logger LOGGER = LogManager.getLogger(SystemEnvironment.class);

    private static SystemEnvironment instance;

    /**
     * @return the instance of the system.
     */
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
    private long ram;

    private SystemEnvironment() {
    }

    /**
     * Updates the parameters of the system environment.
     */
    private void updateParameters() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.error("Could not set computer name.");
        }

        ram = Math.round(hardwareAbstractionLayer.getMemory().getTotal() / Math.pow(2, 30)); // in GiB
        processor = hardwareAbstractionLayer.getProcessor().getProcessorIdentifier().getName();
        cores = hardwareAbstractionLayer.getProcessor().getLogicalProcessorCount();
        os = systemInfo.getOperatingSystem().getFamily();
        kernel = System.getProperty("os.arch");
    }

    /**
     * @return the OS name.
     */
    public String getOs() {
        return os;
    }

    /**
     * @return the processor name.
     */
    public String getProcessor() {
        return processor;
    }

    /**
     * @return the kernel name.
     */
    public String getKernel() {
        return kernel;
    }

    /**
     * @return the amount of RAM in GiB.
     */
    public long getRam() {
        return ram;
    }

    /**
     * @return the logical core count.
     */
    public int getCores() {
        return cores;
    }

    /**
     * @return the computer name.
     */
    public String getComputerName() {
        return computerName;
    }
}
