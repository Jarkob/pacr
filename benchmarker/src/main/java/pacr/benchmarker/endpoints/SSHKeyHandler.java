package pacr.benchmarker.endpoints;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;
import pacr.benchmarker.services.SSHKeyUpdater;

import java.lang.reflect.Type;

/**
 * Receives new SSH private keys and sets them.
 *
 * @author Pavel Zwerschke
 */
@Component
public class SSHKeyHandler implements StompFrameHandler {

    private SSHKeyUpdater sshKeyUpdater;

    /**
     * Creates an instance of SSHKeyHandler.
     * @param sshKeyUpdater is needed for updating the SSH keys.
     */
    public SSHKeyHandler(SSHKeyUpdater sshKeyUpdater) {
        this.sshKeyUpdater = sshKeyUpdater;
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return SSHKeyMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        SSHKeyMessage sshKeyMessage = (SSHKeyMessage) payload;

        sshKeyUpdater.setSSHKey(sshKeyMessage.getSshKey());
    }
}
