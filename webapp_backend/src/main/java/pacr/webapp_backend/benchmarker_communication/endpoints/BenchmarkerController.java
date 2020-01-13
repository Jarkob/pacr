package pacr.webapp_backend.benchmarker_communication.endpoints;

import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.benchmarker_communication.services.IBenchmarkerConfigurationSender;

/**
 * Stub so the code compiles.
 */
@RestController
public class BenchmarkerController implements IBenchmarkerConfigurationSender {

    @Override
    public void sendSSHKey(String sshKey) {

    }

}
