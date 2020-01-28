package pacr.webapp_backend.shared;

/**
 * Creates new passwords.
 */
public interface IPasswordCreator {
    /**
     * Generates a new random admin password and saves its hash.
     * @return the new password (not hashed).
     */
    String newPassword();
}
