package pacr.webapp_backend.benchmarker_communication.services;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkerConfigurator;

@Component
public class BenchmarkerConfigurator implements IBenchmarkerConfigurator {

    private IBenchmarkerConfigurationSender configurationSender;

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
