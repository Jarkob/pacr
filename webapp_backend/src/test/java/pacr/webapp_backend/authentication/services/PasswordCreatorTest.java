package pacr.webapp_backend.authentication.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class PasswordCreatorTest {

    private static final int LENGTH_PASSWORD = 16;

    @Mock
    private final IAuthenticationAccess authenticationAccessMock;

    private final HashGenerator hashGenerator;

    private final PasswordCreator passwordCreator;

    public PasswordCreatorTest() {
        authenticationAccessMock = Mockito.mock(IAuthenticationAccess.class);
        hashGenerator = new HashGenerator();
        passwordCreator = new PasswordCreator(authenticationAccessMock, hashGenerator);
    }

    /**
     * Tests whether newPassword returns a password of the correct size and saves its hash.
     */
    @Test
    void newPassword_shouldBeLongAndSaved() {
        final String password = passwordCreator.newPassword();

        final String hash = hashGenerator.hashPassword(password);
        verify(authenticationAccessMock).setAdminPasswordHash(hash);

        assertEquals(LENGTH_PASSWORD, password.length());
    }
}
