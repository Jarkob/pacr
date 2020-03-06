package pacr.webapp_backend.authentication.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A jwt representation, needed for authentication
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String token;
}
