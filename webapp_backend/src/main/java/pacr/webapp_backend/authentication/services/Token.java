package pacr.webapp_backend.authentication.services;

/**
 * A jwt representation, needed for authentication
 */
public class Token {

    private String token;

    /**
     * Creates an empty token
     */
    public Token() { }

    /**
     * Creates a new token
     * @param token the value of the token
     */
    public Token(String token) {
        this.token = token;
    }

    /**
     * Get the token value
     * @return the value of the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the token value
     * @param token the value of the token
     */
    public void setToken(String token) {
        this.token = token;
    }
}
