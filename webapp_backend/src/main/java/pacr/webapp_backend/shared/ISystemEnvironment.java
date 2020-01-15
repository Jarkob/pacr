package pacr.webapp_backend.shared;

/**
 * Represents a system environment.
 */
public interface ISystemEnvironment {

    /**
     * @return the name of the computer.
     */
    String getComputerName();

    /**
     * @return a description of the used operating system.
     */
    String getOS();

    /**
     * @return the name of the processor.
     */
    String getProcessor();

    /**
     * @return a description of the used kernel.
     */
    String getKernel();

    /**
     * @return the amount of cores available.
     */
    int getCores();

    /**
     * @return the amount of RAM available in Gigabytes.
     */
    long getRamMemory();

}
