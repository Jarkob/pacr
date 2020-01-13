package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Collection;

/**
 * Manages a collection of PACR-Benchmarkers and allows benchmarkers to be registered and unregistered.
 * For each benchmarker the current system environment is saved.
 */
public interface IBenchmarkerHandler {

    /**
     * Adds a new Benchmarker to the collection.
     * @param address the address used to communicate with the benchmarker.
     * @param sysEnvironment the current SystemEnvironment of the benchmarker.
     * @return if the registration was successful.
     */
    boolean registerBenchmarker(String address, SystemEnvironment sysEnvironment);

    /**
     * Removes a Benchmarker from the collection.
     * @param address the address used to communicate with the benchmarker.
     * @return if the removal was successful.
     */
    boolean unregisterBenchmarker(String address);

    /**
     * @return the system environment of each registered Benchmarker.
     */
    Collection<SystemEnvironment> getBenchmarkerSystemEnvironment();

}
