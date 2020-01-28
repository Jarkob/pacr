package pacr.webapp_backend.authentication.services;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IPasswordCreator;

import java.util.Arrays;
import java.util.List;

/**
 * Implements the {@link IPasswordCreator} interface.
 */
@Component
public class PasswordCreator implements IPasswordCreator {

    private static final char[] SPECIAL_CHARS = {'!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', '-', '.', '/',
            ':', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'};
    private static final String CHAR_ERROR = "CHAR ERROR";
    private static final int MIN_NUM_OF_CHARS = 2;
    private static final int PASSWORD_LENGTH = 16;

    private IAuthenticationAccess authenticationAccess;
    private HashGenerator hashGenerator;

    /**
     * Creates a new PasswordCreator.
     * @param authenticationAccess access to authentication information.
     * @param hashGenerator generator of hashes.
     */
    PasswordCreator(IAuthenticationAccess authenticationAccess, HashGenerator hashGenerator) {
        this.authenticationAccess = authenticationAccess;
        this.hashGenerator = hashGenerator;
    }

    @Override
    public String newPassword() {
        String password = generatePassword();
        String passwordHash = hashGenerator.hashPassword(password);

        authenticationAccess.setAdminPasswordHash(passwordHash);

        return password;
    }

    private String generatePassword() {
        List<CharacterRule> rules = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, MIN_NUM_OF_CHARS),
                new CharacterRule(EnglishCharacterData.LowerCase, MIN_NUM_OF_CHARS),
                new CharacterRule(EnglishCharacterData.Digit, MIN_NUM_OF_CHARS),
                new CharacterRule(new CharacterData() {
                    @Override
                    public String getErrorCode() {
                        return CHAR_ERROR;
                    }

                    @Override
                    public String getCharacters() {
                        return new String(SPECIAL_CHARS);
                    }
                }, MIN_NUM_OF_CHARS)
        );

        PasswordGenerator generator = new PasswordGenerator();

        return generator.generatePassword(PASSWORD_LENGTH, rules);
    }
}
