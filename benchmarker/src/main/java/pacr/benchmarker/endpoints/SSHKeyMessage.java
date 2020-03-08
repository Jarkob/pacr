package pacr.benchmarker.endpoints;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A message containing a ssh key that is sent over a websocket connection.
 */
@Getter
@NoArgsConstructor
public class SSHKeyMessage {

    private String sshKey;

}
