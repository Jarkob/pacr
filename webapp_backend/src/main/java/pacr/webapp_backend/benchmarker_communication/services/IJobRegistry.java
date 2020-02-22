package pacr.webapp_backend.benchmarker_communication.services;

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
    BenchmarkerJob getCurrentBenchmarkerJob(String address);

}
