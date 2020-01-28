package pacr.benchmarker;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.SSHKeyUpdater;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test case for SSHKeyUpdater.
 *
 * @author Pavel Zwerschke
 */
public class SSHKeyUpdaterTest {

    private static final String SSH_KEY = "BEGIN SSH PRIVATE KEY...";
    private static final String PATH_TO_TEST_DIR = "/target/test/ssh/";
    private static final String ABS_PATH_TO_TEST_DIR = System.getProperty("user.dir") + PATH_TO_TEST_DIR;
    private static final String PATH_TO_SSH_KEY = PATH_TO_TEST_DIR + "ssh.key";
    private static final String ABS_PATH_TO_SSH_KEY = System.getProperty("user.dir") + PATH_TO_SSH_KEY;

    public static void deleteFolders() {
        File repos = new File(ABS_PATH_TO_TEST_DIR);
        if (repos.exists()) {
            try {
                FileUtils.deleteDirectory(repos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @BeforeEach
    public void setUp() {
        deleteFolders();
        File testDir = new File(ABS_PATH_TO_TEST_DIR);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
    }

    @Test
    public void writeSSHKey() {
        SSHKeyUpdater sshKeyUpdater = new SSHKeyUpdater(PATH_TO_SSH_KEY);

        sshKeyUpdater.setSSHKey(SSH_KEY);

        File file = new File(ABS_PATH_TO_SSH_KEY);
        assertTrue(file.exists());
    }
}
