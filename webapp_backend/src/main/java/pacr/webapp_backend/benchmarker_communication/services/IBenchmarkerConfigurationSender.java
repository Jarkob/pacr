package pacr.webapp_backend.benchmarker_communication.services;

/**
 * Sends new configurations to all registered PACR-Benchmarkers.
 */
public interface IBenchmarkerConfigurationSender {

    /**
     * Sends the current private ssh key to all Benchmarkers.
     * @param sshKey the current private ssh key.
     */
    void sendSSHKey(String sshKey);

}
