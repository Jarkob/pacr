package pacr.webapp_backend.shared;

public interface IAuthenticator {
    boolean authenticate(String token);
}
