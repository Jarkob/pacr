package pacr.webapp_backend.authentication.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashGeneratorTest {

    private static final String PASSWORD = "password";
    private static final String EXPECTED_HASH = "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86";

    private final HashGenerator hashGenerator;

    public HashGeneratorTest() {
        this.hashGenerator = new HashGenerator();
    }

    /**
     * Tests whether hashPassword returns the correct hash.
     */
    @Test
    void hashPassword_shouldReturnHash() {
        final String hash = hashGenerator.hashPassword(PASSWORD);

        assertEquals(EXPECTED_HASH, hash);
    }
}
