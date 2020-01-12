package pacr.webapp_backend.shared;

/**
 * Allows the configuration of all registered PACR-Benchmarkers.
 */
public interface IBenchmarkerConfigurator {

    /**
     * Updates the private ssh key on all registered Benchmarkers.
     * @param sshKey the current private ssh key.
     */
    void updateSSHKey(String sshKey);

}
