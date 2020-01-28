package pacr.webapp_backend.authentication.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class PasswordCheckerTest {

    private static final String PASSWORD = "password";
    private static final String PASSWORD_TWO = "password2";
    private static final String EMPTY = "";

    @Mock
    private IAuthenticationAccess authenticationAccessMock;

    private HashGenerator hashGenerator;

    private PasswordChecker passwordChecker;

    public PasswordCheckerTest() {
        this.authenticationAccessMock = Mockito.mock(IAuthenticationAccess.class);
        this.hashGenerator = new HashGenerator();
        this.passwordChecker = new PasswordChecker(authenticationAccessMock, hashGenerator);
    }

    /**
     * Tests whether checkPassword returns true if the hashes match.
     */
    @Test
    void checkPassword_match_shouldReturnTrue() {
        String adminHash = hashGenerator.hashPassword(PASSWORD);
        when(authenticationAccessMock.getAdminPasswordHash()).thenReturn(adminHash);

        assertTrue(passwordChecker.checkPassword(PASSWORD));
    }

    /**
     * Tests whether checkPassword returns false if the hashes don't match.
     */
    @Test
    void checkPassword_noMatch_shouldReturnFalse() {
        String adminHash = hashGenerator.hashPassword(PASSWORD);
        when(authenticationAccessMock.getAdminPasswordHash()).thenReturn(adminHash);

        assertFalse(passwordChecker.checkPassword(PASSWORD_TWO));
    }

    /**
     * Tests whether checkPassword throws an exception if no password has been set yet.
     */
    @Test
    void checkPassword_emptyAdminPassword_shouldThrowException() {
        when(authenticationAccessMock.getAdminPasswordHash()).thenReturn(EMPTY);

        assertThrows(IllegalStateException.class, () -> passwordChecker.checkPassword(PASSWORD));
    }
}
