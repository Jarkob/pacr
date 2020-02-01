package pacr.webapp_backend.authentication.endpoints;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pacr.webapp_backend.authentication.services.PasswordChecker;
import pacr.webapp_backend.authentication.services.TokenManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginControllerTest {

    private static final String PASSWORD = "password";
    private static final String JWT = "jwt";

    @Mock
    private PasswordChecker passwordCheckerMock;
    @Mock
    private TokenManager tokenManagerMock;

    private LoginController loginController;
    
    public LoginControllerTest() {
        this.passwordCheckerMock = Mockito.mock(PasswordChecker.class);
        this.tokenManagerMock = Mockito.mock(TokenManager.class);
        this.loginController = new LoginController(passwordCheckerMock, tokenManagerMock);
    }

    /**
     * Tests whether login correctly tests the entered password and returns the jwt if it is correct.
     */
//    @Test
//    void login_passwordCorrect_shouldReturnJWT() {
//        when(passwordCheckerMock.checkPassword(PASSWORD)).thenReturn(true);
//        when(tokenManagerMock.generateToken()).thenReturn(JWT);
//
//        ResponseEntity<String> response = loginController.login(PASSWORD);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(JWT, response.getBody());
//    }
//
//    /**
//     * Tests whether login returns no jwt and an unauthorized response if the password is incorrect.
//     */
//    @Test
//    void login_passwordIncorrect_shouldReturnUnauthorizedWithEmptyBody() {
//        when(passwordCheckerMock.checkPassword(PASSWORD)).thenReturn(false);
//
//        ResponseEntity<String> response = loginController.login(PASSWORD);
//
//        verify(tokenManagerMock, never()).generateToken();
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertNull(response.getBody());
//    }
}
