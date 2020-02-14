package pacr.webapp_backend.authentication.services;

import lombok.Getter;
import lombok.Setter;

/**
 * A representation of a password, needed for authentication
 */
@Getter
@Setter
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
}
