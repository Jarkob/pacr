package pacr.webapp_backend.authentication.services;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Generates hashes of strings.
 */
@Component
public class HashGenerator {

    /**
     * The identifier of the desired hashing algorithm.
     */
    static final String ALGORITHM = "SHA-512";

    private static final String FORMAT_TWO_DIGIT_HEX = "%02x";

    /**
     * Hashes a password.
     * @param password the password.
     * @return the hash of the password.
     */
    String hashPassword(String password) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("no hashing algorithm " + ALGORITHM);
        }

        byte[] hashBytes = messageDigest.digest(password.getBytes());
        StringBuilder hashBuilder = new StringBuilder();

        for (byte hashByte : hashBytes) {
            // convert bytes to hexadecimal
            hashBuilder.append(String.format(FORMAT_TWO_DIGIT_HEX, hashByte));
        }

        return hashBuilder.toString();
    }
}
