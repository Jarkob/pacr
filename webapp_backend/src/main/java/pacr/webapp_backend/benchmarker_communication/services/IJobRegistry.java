package pacr.webapp_backend.benchmarker_communication.services;

import pacr.webapp_backend.shared.IJob;

/**
 * Manages all jobs that are currently dispatched to PACR-Benchmarkers.
 */
public interface IJobRegistry {

    /**
     * Gets the current job of the benchmarker with the given address.
     *
     * @param address the address of the benchmarker.
     * @return the current job of the benchmarker or null if no job is assigned.
     */
    IJob getCurrentBenchmarkerJob(String address);

}
