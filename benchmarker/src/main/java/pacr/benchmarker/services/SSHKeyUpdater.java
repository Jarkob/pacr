package pacr.benchmarker.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Updates the SSH private key for the benchmarker.
 *
 * @author Pavel Zwerschke
 */
@Component
public class SSHKeyUpdater {

    private static final Logger LOGGER = LogManager.getLogger(SSHKeyUpdater.class);

    private String pathToPrivateKey;

    /**
     * Creates a new instance of SSHKeyUpdater.
     * @param pathToPrivateKey is the relative path to the private key.
     */
    public SSHKeyUpdater(@Value("${privateKeyPath}") String pathToPrivateKey) {
        this.pathToPrivateKey = System.getProperty("user.dir") + pathToPrivateKey;
    }

    /**
     * Writes the SSH private key.
     * @param key is the private key.
     */
    public void setSSHKey(String key) {
        LOGGER.info("Saving private key.");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathToPrivateKey));
            writer.write(key);
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Could not write file.");
        }

    }

}
