package pacr.webapp_backend.shared;

/**
 * Represents a system environment.
 */
public interface ISystemEnvironment {

    /**
     * @return the name of the computer. Returns null if there was a detection error.
     */
    String getComputerName();

    /**
     * @return a description of the used operating system. Returns null if there was a detection error.
     */
    String getOS();

    /**
     * @return the name of the processor. Returns null if there was a detection error.
     */
    String getProcessor();

    /**
     * @return a description of the used kernel. Returns null if there was a detection error.
     */
    String getKernel();

    /**
     * @return the amount of cores available. Returns 0 if there was a detection error.
     */
    int getCores();

    /**
     * @return the amount of RAM available in Gigabytes. Returns 0 if there was a detection error.
     */
    long getRamMemory();

}
