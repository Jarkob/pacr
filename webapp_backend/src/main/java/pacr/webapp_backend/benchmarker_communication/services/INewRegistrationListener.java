package pacr.webapp_backend.benchmarker_communication.services;

/**
 * An IJobReceiver is able to receive updates so a job execution is started.
 */
interface INewRegistrationListener {

    /**
     * Notifies the listener that a new registration happened.
     */
    void newRegistration();

}
