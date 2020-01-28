package pacr.benchmarker.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Pavel Zwerschke
 */
public class SSHKeyUpdater {

    private static final Logger LOGGER = LogManager.getLogger(SSHKeyUpdater.class);

    private String pathToPrivateKey;

    public SSHKeyUpdater() {
        pathToPrivateKey = System.getProperty("user.dir") + "/ssh.key";
    }

    public SSHKeyUpdater(String pathToPrivateKey) {
        this.pathToPrivateKey = System.getProperty("user.dir") + pathToPrivateKey;
    }

    public void setSSHKey(String key) {
        System.out.println("Save key " + key);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathToPrivateKey));
            writer.write(key);
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Could not write file.");
        }

    }

}
