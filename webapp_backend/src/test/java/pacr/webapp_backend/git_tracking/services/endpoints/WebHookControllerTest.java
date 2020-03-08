package pacr.webapp_backend.git_tracking.services.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.endpoints.WebHookController;
import pacr.webapp_backend.git_tracking.services.GitTracking;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for WebHookController.
 *
 * @author Pavel Zwerschke
 */
public class WebHookControllerTest {

    private WebHookController webHookController;
    @Mock
    private GitTracking gitTracking;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        webHookController = new WebHookController(gitTracking);
    }

    @Test
    public void pullFromRepository() {
        webHookController.pullFromRepository(5);

        verify(gitTracking).pullFromRepository(5);
    }

    @Test
    public void pullFromRepositoryNotExisting() {
        when(webHookController.pullFromRepository(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(ResponseStatusException.class, () -> webHookController.pullFromRepository(5));
    }

}
