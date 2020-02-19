package pacr.webapp_backend.benchmarker_communication.endpoints;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A message containing a ssh key that is sent over a websocket connection.
 */
@Getter
@NoArgsConstructor
public class SSHKeyMessage {

    private String sshKey;

    /**
     * Creates a new SSHKeyMessage with the given key.
     *
     * @param sshKey the ssh key that is sent.
     */
    public SSHKeyMessage(String sshKey) {
        this.sshKey = sshKey;
    }
}
