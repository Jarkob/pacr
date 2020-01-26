package pacr.webapp_backend.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.authentication.IAuthenticationAccess;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Implements the IAuthenticationAccess interface and saves data in two files.
 * This class follows a different naming scheme because data is not stored in the database.
 */
@Component
public class AuthenticationStorage implements IAuthenticationAccess {

    /**
     * The directory where java was run from.
     */
    public static final String USER_DIR = "user.dir";

    private File adminPasswordHashFile;
    private File secretFile;

    /**
     * Creates a new AuthenticationStorage. Creates empty file if necessary.
     * @param adminPasswordHashPath path to the adminPasswordHash file.
     * @param secretPath path to the secret file.
     * @throws IOException if creating a new file fails.
     */
    public AuthenticationStorage(@NotNull @Value("${adminPasswordHashPath}") String adminPasswordHashPath,
                                 @NotNull @Value("${secretPath}") String secretPath) throws IOException {
        Objects.requireNonNull(adminPasswordHashPath);
        Objects.requireNonNull(secretPath);

        this.adminPasswordHashFile = new File(System.getProperty(USER_DIR) + adminPasswordHashPath);
        this.secretFile = new File(System.getProperty(USER_DIR) + secretPath);

        // creates no new file if it already exists.
        adminPasswordHashFile.createNewFile();
        secretFile.createNewFile();
    }

    @Override
    public String getAdminPasswordHash() {
        return readFromFile(adminPasswordHashFile);
    }

    @Override
    public String getSecret() {
        return readFromFile(secretFile);
    }

    @Override
    public void setAdminPasswordHash(@NotNull String passwordHash) {
        Objects.requireNonNull(passwordHash);
        writeToFile(adminPasswordHashFile, passwordHash);
    }

    @Override
    public void setSecret(@NotNull String secret) {
        Objects.requireNonNull(secret);
        writeToFile(secretFile, secret);
    }

    private String readFromFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("could not read from file " + file.getPath());
        }
    }

    private void writeToFile(File file, String content) {
        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new IllegalStateException("could not write to file " + file.getPath());
        }
    }
}
