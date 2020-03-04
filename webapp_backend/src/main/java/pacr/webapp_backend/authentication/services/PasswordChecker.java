package pacr.webapp_backend.authentication.services;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Checks validity of a given password.
 */
@Component
public class PasswordChecker {

    private IAuthenticationAccess authenticationAccess;
    private HashGenerator hashGenerator;

    /**
     * Creates a PasswordChecker.
     * @param authenticationAccess access to authentication information.
     * @param hashGenerator generator of hashes.
     */
    PasswordChecker(final IAuthenticationAccess authenticationAccess, final HashGenerator hashGenerator) {
        this.authenticationAccess = authenticationAccess;
        this.hashGenerator = hashGenerator;
    }

    /**
     * Checks a given password for validity against the hash of the admin password.
     * @param enteredPassword the entered password. Cannot be null, empty or blank.
     * @return {@code true} if the entered password matches the admin password, otherwise {@code false}.
     */
    public boolean checkPassword(@NotNull final String enteredPassword) {
        if (!StringUtils.hasText(enteredPassword)) {
            throw new IllegalArgumentException("the entered password cannot be null, empty or blank");
        }

        final String adminPasswordHash = authenticationAccess.getAdminPasswordHash();

        if (!StringUtils.hasText(adminPasswordHash)) {
            throw new IllegalStateException("no password has been set yet");
        }

        final String passwordHash = hashGenerator.hashPassword(enteredPassword);

        return adminPasswordHash.equals(passwordHash);
    }
}