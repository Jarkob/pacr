package pacr.webapp_backend.authentication.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A jwt representation, needed for authentication
 */
@Getter
@Setter
@NoArgsConstructor
public class Token {

    private String token;

    /**
     * Creates a new token
     * @param token the value of the token
     */
    public Token(String token) {
        this.token = token;
    }
}
