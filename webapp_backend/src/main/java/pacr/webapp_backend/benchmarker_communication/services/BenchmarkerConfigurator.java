package pacr.webapp_backend.benchmarker_communication.services;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkerConfigurator;

/**
 * Handles the configuration of all registered PACR-Benchmarkers.
 */
@Component
public class BenchmarkerConfigurator implements IBenchmarkerConfigurator {

    private IBenchmarkerConfigurationSender configurationSender;

    /**
     * Creates a new BenchmarkerConfigurator with a ConfigurationSender.
     * @param configurationSender is used to send the configuration data to all registered Benchmarkers.
     */
    public BenchmarkerConfigurator(IBenchmarkerConfigurationSender configurationSender) {
        this.configurationSender = configurationSender;
    }

    @Override
    public void updateSSHKey(String sshKey) {
        verifySSHKey(sshKey);

        configurationSender.sendSSHKey(sshKey);
    }

    private void verifySSHKey(String sshKey) {
        if (sshKey == null || sshKey.isEmpty() || sshKey.isBlank()) {
            throw new IllegalArgumentException("The sshKey is not valid.");
        }
    }
}
