package pacr.webapp_backend.shared;

/**
 * Represents a system environment.
 */
public interface ISystemEnvironment {

    /**
     * @return a description of the used operating system.
     */
    String getOS();

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
