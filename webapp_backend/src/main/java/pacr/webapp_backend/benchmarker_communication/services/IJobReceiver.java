package pacr.webapp_backend.benchmarker_communication.services;

/**
 * An IJobReceiver is able to receive updates so a job execution is started.
 */
interface IJobReceiver {

    /**
     * Triggers the execution of a job if one is available.
     */
    void executeJob();

}
