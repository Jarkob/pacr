package pacr.webapp_backend.git_tracking.services.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.git_tracking.endpoints.PullIntervalController;
import pacr.webapp_backend.git_tracking.services.IPullIntervalAccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for PullIntervalController.
 *
 * @author Pavel Zwerschke
 */
public class PullIntervalControllerTest {

    private PullIntervalController pullIntervalController;
    @Mock
    private IPullIntervalAccess access;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pullIntervalController = new PullIntervalController(access);
    }

    @Test
    public void getPullInterval() {
        when(access.getPullInterval()).thenReturn(5);
        assertEquals(5, pullIntervalController.getPullInterval());
        verify(access).getPullInterval();
    }

    @Test
    public void setPullInterval() {
        pullIntervalController.setPullInterval(5);
        verify(access).setPullInterval(5);
    }

}
