package pacr.webapp_backend.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthenticationStorageTest {

    private static final String PW_HASH = "hash";
    private static final byte[] SECRET = { -128, -55, -1, 0, 1, 55, 127 };

    private AuthenticationStorage authenticationStorage;

    private File adminPasswordHashFile;
    private File secretFile;

    @Autowired
    public AuthenticationStorageTest(AuthenticationStorage authenticationStorage,
                                     @Value("${adminPasswordHashPath}") String adminPasswordHashPath,
                                     @Value("${secretPath}") String secretPath) {
        this.authenticationStorage = authenticationStorage;
        this.adminPasswordHashFile = new File(System.getProperty(AuthenticationStorage.USER_DIR)
                + adminPasswordHashPath);
        this.secretFile = new File(System.getProperty(AuthenticationStorage.USER_DIR) + secretPath);
    }

    @AfterEach
    void cleanUp() throws IOException {
        secretFile.delete();
        secretFile.createNewFile();

        adminPasswordHashFile.delete();
        adminPasswordHashFile.createNewFile();
    }

    /**
     * Tests whether the storage files exist after the constructor of AuthenticationStorage has been called.
     */
    @Test
    void constructor_shouldHaveCreatedFiles() {
        assertTrue(adminPasswordHashFile.exists() && secretFile.exists());
    }

    /**
     * Tests whether getAdminPasswordHash returns empty string if the file is empty.
     */
    @Test
    void getAdminPasswordHash_emptyFile_shouldReturnEmptyString() {
        String adminPasswordHash = authenticationStorage.getAdminPasswordHash();

        assertNotNull(adminPasswordHash);
        assertTrue(adminPasswordHash.isEmpty());
    }

    /**
     * Tests whether getSecret returns empty array if the file is empty.
     */
    @Test
    void getSecret_emptyFile_shouldReturnEmptyString() {
        byte[] secret = authenticationStorage.getSecret();

        assertEquals(0, secret.length);
    }

    /**
     * Tests whether setAdminPasswordHash correctly edits the file.
     * @throws IOException if reading the file fails.
     */
    @Test
    void setAdminPasswordHash_shouldAlterFile() throws IOException {
        authenticationStorage.setAdminPasswordHash(PW_HASH);

        String fileContent = Files.readString(adminPasswordHashFile.toPath());

        assertEquals(PW_HASH, fileContent);
    }


    /**
     * Tests whether setSecret correctly edits the file.
     * @throws IOException if reading the file fails.
     */
    @Test
    void setSecret_shouldAlterFile() throws IOException {
        authenticationStorage.setSecret(SECRET);

        byte[] fileContent = Files.readAllBytes(secretFile.toPath());

        for (int i = 0; i < fileContent.length; ++i) {
            assertEquals(SECRET[i], fileContent[i]);
        }
    }

    /**
     * Tests whether getAdminPasswordHash returns the same value that has previously been set.
     */
    @Test
    void getAdminPasswordHash_hasBeenSet_shouldReturnSetValue() {
        authenticationStorage.setAdminPasswordHash(PW_HASH);

        String adminPasswordHash = authenticationStorage.getAdminPasswordHash();

        assertEquals(PW_HASH, adminPasswordHash);
    }

    /**
     * Tests whether getSecret returns the same value that has previously been set. Even if it was set twice.
     */
    @Test
    void getSecret_hasBeenSet_shouldReturnSetValue() {
        authenticationStorage.setSecret(SECRET);

        authenticationStorage.setSecret(SECRET);

        byte[] secret = authenticationStorage.getSecret();

        for (int i = 0; i < secret.length; ++i) {
            assertEquals(SECRET[i], secret[i]);
        }
    }
}
