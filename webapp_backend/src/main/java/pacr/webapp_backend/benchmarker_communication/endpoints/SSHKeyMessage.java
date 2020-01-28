package pacr.webapp_backend.benchmarker_communication.endpoints;

/**
 * A message containing a ssh key that is sent over a websocket connection.
 */
public class SSHKeyMessage {

    private String sshKey;

    /**
     * Creates an empty SSHKeyMessage.
     *
     * Necessary for spring to work.
     */
    public SSHKeyMessage() {
    }

    /**
     * Creates a new SSHKeyMessage with the given key.
     *
     * @param sshKey the ssh key that is sent.
     */
    public SSHKeyMessage(String sshKey) {
        this.sshKey = sshKey;
    }

    public String getSshKey() {
        return sshKey;
    }
}
