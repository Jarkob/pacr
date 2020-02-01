package pacr.webapp_backend.authentication.services;

public class Token {
    public Token() {

    }

    public Token(String token) {
        this.token = token;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
