package pacr.webapp_backend.authentication.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.crypto.spec.SecretKeySpec;

/**
 * Creates and checks JSON Web Tokens.
 */
@Component
public class TokenManager implements IAuthenticator {

    private static final Logger LOGGER = LogManager.getLogger(TokenManager.class);

    private static final String ISSUER_PACR = "PACR-Backend";
    private static final String AUDIENCE_ADMIN = "admin";
    private static final int SECRET_LENGTH = 128;

    private final IAuthenticationAccess authenticationAccess;

    /**
     * Creates a new TokenManager.
     * @param authenticationAccess access to authentication data.
     */
    public TokenManager(final IAuthenticationAccess authenticationAccess) {
        this.authenticationAccess = authenticationAccess;
    }

    /**
     * Generates a new token with the current time as the issue time. This token will never expire.
     * Enters IAuthenticationAccess monitor.
     * @return the jwt.
     */
    public String generateToken() {
        final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] secret;

        synchronized (IAuthenticationAccess.class) {
            secret = authenticationAccess.getSecret();

            // generate and save secret if necessary
            if (secret.length == 0) {
                secret = generateSecret();
                authenticationAccess.setSecret(secret);
            }
        }

        final Key signingKey = new SecretKeySpec(secret, signatureAlgorithm.getJcaName());

        final JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer(ISSUER_PACR)
                .setAudience(AUDIENCE_ADMIN)
                .signWith(signingKey, signatureAlgorithm);

        return builder.compact();
    }

    @Override
    public boolean authenticate(final String token) {
        final byte[] secret = authenticationAccess.getSecret();

        if (secret == null || secret.length == 0) {
            // no secret has been set, therefore no one has created a token yet that could be authenticated
            return false;
        }

        final Claims claims;

        try {
            // This line will throw an exception if it is not a signed JWS (as expected)
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token).getBody();
        } catch (final ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException
                | IllegalArgumentException e) {
            LOGGER.error("Exception while parsing claims of token: '{}'", e.getMessage());

            return false;
        }

        if (claims == null) {
            return false;
        }

        return claims.getAudience().equals(AUDIENCE_ADMIN) && claims.getIssuer().equals(ISSUER_PACR);
    }

    private byte[] generateSecret() {
        final byte[] secretBytes = new byte[SECRET_LENGTH];
        final SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(secretBytes);

        return secretBytes;
    }
}
