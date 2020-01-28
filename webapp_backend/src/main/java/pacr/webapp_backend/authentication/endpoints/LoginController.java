package pacr.webapp_backend.authentication.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.authentication.services.PasswordChecker;
import pacr.webapp_backend.authentication.services.TokenManager;

import javax.validation.constraints.NotNull;

/**
 * Generates valid jwts for clients with the proper password.
 */
@RestController
public class LoginController {

    private PasswordChecker passwordChecker;
    private TokenManager tokenManager;

    /**
     * Creates a new LoginController.
     * @param passwordChecker checks passwords against the admin password.
     * @param tokenManager generates jwts.
     */
    LoginController(PasswordChecker passwordChecker, TokenManager tokenManager) {
        this.passwordChecker = passwordChecker;
        this.tokenManager = tokenManager;
    }

    /**
     * Checks whether the given password is valid and returns a non-expiring JWT.
     * @param password the password.
     * @return HTTP code 200 (ok) with the token as body if the password was correct. Otherwise HTTP code 401
     * (unauthorized) with empty body is returned.
     */
    @GetMapping("/login")
    public ResponseEntity<String> login(@NotNull @RequestHeader(name = "password") String password) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("password cannot be null, empty or blank");
        }

        if (passwordChecker.checkPassword(password)) {
            return ResponseEntity.ok().body(tokenManager.generateToken());
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
