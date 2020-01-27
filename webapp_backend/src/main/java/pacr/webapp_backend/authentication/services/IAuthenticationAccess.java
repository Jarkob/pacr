package pacr.webapp_backend.authentication.services;

import javax.validation.constraints.NotNull;

/**
 * Provides access to information for the authentication component.
 */
public interface IAuthenticationAccess {

    /**
     * @return Gets the hashed password for admin authentication.
     */
    String getAdminPasswordHash();

    /**
     * @return Gets the secret for signing JWTs.
     */
    String getSecret();

    /**
     * Sets the hashed password for the admin.
     * @param passwordHash the hash of the password. Cannot be null.
     */
    void setAdminPasswordHash(@NotNull String passwordHash);

    /**
     * Sets the secret for signing JWTs.
     * @param secret the secret. Cannot be null.
     */
    void setSecret(@NotNull String secret);
}
