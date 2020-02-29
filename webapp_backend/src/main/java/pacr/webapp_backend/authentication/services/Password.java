package pacr.webapp_backend.authentication.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A representation of a password, needed for authentication
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Password {
    private String password;
}
