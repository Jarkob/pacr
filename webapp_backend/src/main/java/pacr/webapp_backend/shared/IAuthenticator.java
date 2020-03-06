package pacr.webapp_backend.shared;

/**
 * Authenticates jwts.
 */
public interface IAuthenticator {
    /**
     * Checks validity of a given jwt. Throws exception if it is not a signed jws (as expected).
     * @param token the token to authenticate.
     * @return {@code true} if the token is signed with the secret and has the correct issuer and audience values,
     *      otherwise {@code false}.
     */
    boolean authenticate(String token);
}
