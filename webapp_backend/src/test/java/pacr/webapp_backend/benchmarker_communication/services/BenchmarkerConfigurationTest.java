package pacr.webapp_backend.benchmarker_communication.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

public class BenchmarkerConfigurationTest {

    @Mock
    private IBenchmarkerConfigurationSender configurationSender;

    private BenchmarkerConfigurator configurator;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        configurator = new BenchmarkerConfigurator(configurationSender);
    }

    @Test
    void updateSSHKey_noError() {
        final String SSH_KEY = "this is a test ssh key";

        configurator.updateSSHKey(SSH_KEY);

        verify(configurationSender).sendSSHKey(SSH_KEY);
    }

    @Test
    void updateSSHKey_null() {
        assertThrows(IllegalArgumentException.class, () -> {
            configurator.updateSSHKey(null);
        });
    }

    @Test
    void updateSSHKey_empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            configurator.updateSSHKey("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            configurator.updateSSHKey(" ");
        });
    }
}
