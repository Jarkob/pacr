package pacr.webapp_backend.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.authentication.services.IAuthenticationAccess;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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

    private static final String READ_ERROR = "could not read from file ";
    private static final String WRITE_ERROR = "could not write to file ";

    private final File adminPasswordHashFile;
    private final File secretFile;

    /**
     * Creates a new AuthenticationStorage. Creates empty file if necessary.
     * @param adminPasswordHashPath path to the adminPasswordHash file.
     * @param secretPath path to the secret file.
     * @throws IOException if creating a new file fails.
     */
    public AuthenticationStorage(@NotNull @Value("${adminPasswordHashPath}") final String adminPasswordHashPath,
                                 @NotNull @Value("${secretPath}") final String secretPath) throws IOException {
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
        try {
            return Files.readString(adminPasswordHashFile.toPath());
        } catch (final IOException e) {
            throw new IllegalStateException(READ_ERROR + adminPasswordHashFile.getPath());
        }
    }

    @Override
    public byte[] getSecret() {
        try {
            return Files.readAllBytes(secretFile.toPath());
        } catch (final IOException e) {
            throw new IllegalStateException(READ_ERROR + secretFile.getPath());
        }
    }

    @Override
    public void setAdminPasswordHash(@NotNull final String passwordHash) {
        Objects.requireNonNull(passwordHash);
        try (final FileWriter writer = new FileWriter(adminPasswordHashFile, false)) {
            writer.write(passwordHash);
        } catch (final IOException e) {
            throw new IllegalStateException(WRITE_ERROR + adminPasswordHashFile.getPath());
        }
    }

    @Override
    public void setSecret(@NotNull final byte[] secret) {
        Objects.requireNonNull(secret);
        try (final OutputStream os = new FileOutputStream(secretFile)) {
            os.write(secret);
        } catch (final FileNotFoundException e) {
            throw new IllegalStateException("could not find file " + secretFile.getPath());
        } catch (final IOException e) {
            throw new IllegalStateException(WRITE_ERROR + secretFile.getPath());
        }
    }

}
