package pacr.webapp_backend.authentication.services;

public class Password {
    public Password() {

    }

    public Password(String password) {
        this.password = password;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
