package pacr.webapp_backend.git_tracking.endpoints;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.services.SSHKeyProvider;

/**
 * This class represents the SSH-Key Controller.
 * It provides REST mappings to get the SSH Public Key and
 * to send the SSH Private Key to the benchmarkers.
 *
 * @author Pavel Zwerschke
 */
@RestController
public class SSHKeyController {

    private SSHKeyProvider provider;

    /**
     * Creates an instance of SSHKeyController.
     *
     * @param provider is the SSHKeyProvider that provides the
     *                 SSH Public Key and sends the private Key
     *                 to the benchmarkers.
     */
    public SSHKeyController(final SSHKeyProvider provider) {
        this.provider = provider;
    }

    /**
     * Returns the SSH Public Key.
     * @return SSH Public Key.
     */
    @RequestMapping(value = "/ssh/public-key", method = RequestMethod.GET)
    public ResponseEntity<String> getSSHPublicKey() {
        try {
            return ResponseEntity.ok(provider.getSSHPublicKey());
        } catch (final IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Sends the SSH Private Key to the Benchmarker.
     * @return OK (200) if the key was sent successfully,
     *         INTERNAL_SERVER_ERROR (500) when an exception occurred.
     */
    @RequestMapping("/ssh/send-to-benchmarkers")
    public ResponseEntity<Object> sendPrivateKeyToBenchmarker() {
        try {
            provider.sendPrivateKeyToBenchmarker();
        } catch (final IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }
}