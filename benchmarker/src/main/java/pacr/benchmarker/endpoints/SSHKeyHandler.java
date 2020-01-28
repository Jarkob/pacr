package pacr.benchmarker.endpoints;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import pacr.benchmarker.services.SSHKeyUpdater;

import java.lang.reflect.Type;

/**
 * @author Pavel Zwerschke
 */
public class SSHKeyHandler implements StompFrameHandler {

    private SSHKeyUpdater sshKeyUpdater;

    public SSHKeyHandler() {
        this.sshKeyUpdater = new SSHKeyUpdater();
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return SSHKeyMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        SSHKeyMessage sshKeyMessage = (SSHKeyMessage)payload;

        sshKeyUpdater.setSSHKey(sshKeyMessage.getSshKey());
    }
}
