package pacr.webapp_backend.authentication;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IAuthenticator;

/**
 * Mock for an IAuthenticator which always returns true.
 */
@Component
public class AlwaysTrueAuthenticator implements IAuthenticator {
    @Override
    public boolean authenticate(String token) {
        return true;
    }
}
