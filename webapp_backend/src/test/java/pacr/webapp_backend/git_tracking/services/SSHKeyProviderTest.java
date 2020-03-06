package pacr.webapp_backend.git_tracking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.shared.IBenchmarkerConfigurator;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/**
 * Tests for SSH Key provider.
 *
 * @author Pavel Zwerschke
 */
public class SSHKeyProviderTest {

    private SSHKeyProvider keyProvider;

    @Mock
    private IBenchmarkerConfigurator configuratorMock;

    private static final String privateKeyPath
            = "/src/test/resources/pacr/webapp_backend/git_tracking/services/git/id_rsa";
    private static final String publicKeyPath
            = "/src/test/resources/pacr/webapp_backend/git_tracking/services/git/id_rsa.pub";

    @BeforeEach
    void initialize() {
        MockitoAnnotations.initMocks(this);

        try {
            keyProvider = new SSHKeyProvider(privateKeyPath, publicKeyPath, configuratorMock);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found.");
        }
    }

    @Test
    void getPublicKeyTest() {
        try {
            final BufferedReader expected = new BufferedReader(
                    new FileReader(System.getProperty("user.dir") + publicKeyPath));
            final BufferedReader actual = new BufferedReader(new StringReader(keyProvider.getSSHPublicKey()));
            assertReaders(expected, actual);
        } catch (final IOException e) {
            fail();
        }
    }

    @Test
    void getPrivateKeyTest() {
        try {
            final BufferedReader expected = new BufferedReader(
                    new FileReader(System.getProperty("user.dir") + privateKeyPath));

            final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

            keyProvider.sendPrivateKeyToBenchmarker();

            verify(configuratorMock).updateSSHKey(argumentCaptor.capture());
            final String privateKey = argumentCaptor.getValue();

            final BufferedReader actual = new BufferedReader(new StringReader(privateKey));
            assertReaders(expected, actual);
        } catch (final IOException e) {
            fail();
        }
    }

    @Test
    void wrongInitialization() {
        assertThrows(FileNotFoundException.class, () -> {
            keyProvider = new SSHKeyProvider("wrongpath", "wrongpath",
                    Mockito.mock(IBenchmarkerConfigurator.class));
        });
    }

    public static void assertReaders(final BufferedReader expected,
                                     final BufferedReader actual) throws IOException {
        String line;
        while ((line = expected.readLine()) != null) {
            assertEquals(line, actual.readLine());
        }

        assertNull(actual.readLine(), "Actual had more lines then the expected.");
        assertNull(expected.readLine(), "Expected had more lines then the actual.");
    }
}
