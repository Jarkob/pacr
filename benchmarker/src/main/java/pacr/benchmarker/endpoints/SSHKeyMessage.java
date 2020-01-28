package pacr.benchmarker.endpoints;

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
     * @return the ssh key that was sent.
     */
    public String getSshKey() {
        return sshKey;
    }
}
