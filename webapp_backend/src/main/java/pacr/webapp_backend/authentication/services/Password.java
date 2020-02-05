package pacr.webapp_backend.authentication.services;

/**
 * A representation of a password, needed for authentication
 */
public class Password {

    private String password;

    /**
     * Creates an empty password
     */
    public Password() { }

    /**
     * Creates a new password
     * @param password the value of the password
     */
    public Password(String password) {
        this.password = password;
    }

    /**
     * Get the value of the password
     * @return the value of the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of the password
     * @param password the value of the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
