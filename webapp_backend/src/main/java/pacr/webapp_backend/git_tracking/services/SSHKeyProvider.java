package pacr.webapp_backend.git_tracking.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkerConfigurator;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Objects;

/**
 * This class represents the SSH-Key Provider.
 * It provides the SSH Public Key and
 * can send the SSH Private Key to the benchmarkers.
 *
 * @author Pavel Zwerschke
 */
@Component
public class SSHKeyProvider {

    private static final Logger LOGGER = LogManager.getLogger(SSHKeyProvider.class);

    private IBenchmarkerConfigurator configurator;
    private File privateKeyFile;
    private File publicKeyFile;

    /**
     * Initializes a SSHKeyProvider.
     * @param privateKeyPath is the path to the SSH Private Key File.
     * @param publicKeyPath is the path to the SSH Public Key File.
     * @param configurator is the IBenchmarkerConfigurator needed to
     *                     send the SSH Private Keys to the benchmarkers.
     * @throws FileNotFoundException when the SSH Public Key or
     *         the SSH Private Key was not found.
     */
    public SSHKeyProvider(@NotNull @Value("${privateKeyPath}") String privateKeyPath,
                          @NotNull @Value("${publicKeyPath}") String publicKeyPath,
                          @NotNull IBenchmarkerConfigurator configurator) throws FileNotFoundException {
        Objects.requireNonNull(privateKeyPath);
        Objects.requireNonNull(publicKeyPath);
        Objects.requireNonNull(configurator);

        this.configurator = configurator;
        this.privateKeyFile = new File(System.getProperty("user.dir") + privateKeyPath);
        this.publicKeyFile = new File(System.getProperty("user.dir") + publicKeyPath);

        if (!privateKeyFile.exists()) {
            throw new FileNotFoundException("SSH Private Key not found.");
        }
        if (!publicKeyFile.exists()) {
            throw new FileNotFoundException("SSH Public Key not found.");
        }
    }

    /**
     * Returns the SSH Public Key.
     * @return SSH Public Key as a String.
     * @throws IOException when the Public Key file could not be read.
     */
    public String getSSHPublicKey() throws IOException {
        return readFile(publicKeyFile);
    }

    /**
     * Sends the SSH Private Key to the benchmarkers.
     * @throws IOException when the Private Key file could not be read.
     */
    public void sendPrivateKeyToBenchmarker() throws IOException {
        LOGGER.info("Sending SSH private key to all benchmarkers.");

        configurator.updateSSHKey(readFile(privateKeyFile));
    }

    private String readFile(File file) throws IOException {
        assert file != null;

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();

        String line = br.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = br.readLine();
        }

        return sb.toString();
    }

}
