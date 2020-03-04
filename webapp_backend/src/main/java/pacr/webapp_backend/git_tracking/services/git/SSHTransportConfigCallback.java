package pacr.webapp_backend.git_tracking.services.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * This is an implementation of TransportConfigCallback with SSH keys.
 *
 * @author Pavel Zwerschke
 */
@Component
public class SSHTransportConfigCallback implements TransportConfigCallback {

    private String pathToPrivateKey;

    /**
     * Creates an instance of SShTransportConfigCallback.
     * @param pathToPrivateKey is the path (relative from user.dir) to the SSH private key.
     */
    public SSHTransportConfigCallback(@NotNull @Value("${privateKeyPath}") final String pathToPrivateKey) {
        Objects.requireNonNull(pathToPrivateKey);

        this.pathToPrivateKey = System.getProperty("user.dir") + pathToPrivateKey;
    }

    private final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
        @Override
        protected void configure(final OpenSshConfig.Host hc, final Session session) {
            session.setConfig("StrictHostKeyChecking", "no");
        }

        @Override
        protected JSch createDefaultJSch(final FS fs) throws JSchException {
            final JSch jSch = super.createDefaultJSch(fs);
            // removes all ssh identities of the pc
            jSch.removeAllIdentity();
            jSch.addIdentity(pathToPrivateKey);
            return jSch;
        }
    };


    @Override
    public void configure(final Transport transport) {
        final SshTransport sshTransport = (SshTransport) transport;
        sshTransport.setSshSessionFactory(sshSessionFactory);
    }
}
