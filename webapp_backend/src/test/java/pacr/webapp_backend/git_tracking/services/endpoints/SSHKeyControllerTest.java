package pacr.webapp_backend.git_tracking.services.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.endpoints.SSHKeyController;
import pacr.webapp_backend.git_tracking.services.SSHKeyProvider;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Test cases for SSHKeyController.
 *
 * @author Pavel Zwerschke
 */
public class SSHKeyControllerTest {

    private SSHKeyController sshKeyController;
    @Mock
    private SSHKeyProvider sshKeyProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sshKeyController = new SSHKeyController(sshKeyProvider);
    }

    @Test
    public void sendPrivateKeyToBenchmarker() throws IOException {
        sshKeyController.sendPrivateKeyToBenchmarker();
        verify(sshKeyProvider).sendPrivateKeyToBenchmarker();
    }

    @Test
    public void getSSHKey() throws IOException {
        String key = "privateKey";
        when(sshKeyProvider.getSSHPublicKey()).thenReturn(key);
        assertEquals(key, sshKeyController.getSSHPublicKey().getBody());
        verify(sshKeyProvider).getSSHPublicKey();
    }

    @Test
    public void responseStatusException() throws IOException {
        when(sshKeyProvider.getSSHPublicKey()).thenThrow(IOException.class);
        assertThrows(ResponseStatusException.class, () -> sshKeyController.getSSHPublicKey());

        doThrow(IOException.class).when(sshKeyProvider).sendPrivateKeyToBenchmarker();
        assertThrows(ResponseStatusException.class, () -> sshKeyController.sendPrivateKeyToBenchmarker());
    }

}
