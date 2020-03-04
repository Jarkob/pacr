package pacr.webapp_backend.scheduler.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Pageable;
import pacr.webapp_backend.scheduler.services.Scheduler;
import pacr.webapp_backend.shared.IAuthenticator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SchedulerControllerTest {

    private static final String JOB_ID = "jobID";
    private static final String JOB_GROUP = "jobGroup";

    private SchedulerController schedulerController;

    @Mock
    private Scheduler scheduler;

    @Mock
    private IAuthenticator authenticator;

    @Mock
    private Pageable pageable;

    @Spy
    private PrioritizeMessage prioritizeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(prioritizeMessage.getGroupTitle()).thenReturn(JOB_GROUP);
        when(prioritizeMessage.getJobID()).thenReturn(JOB_ID);
        when(prioritizeMessage.validate()).thenReturn(true);

        this.schedulerController = new SchedulerController(scheduler, authenticator);
    }

    @Test
    void SchedulerController_noError() {
        assertDoesNotThrow(() -> {
            final SchedulerController schedulerController = new SchedulerController(scheduler, authenticator);
        });
    }

    @Test
    void SchedulerController_nullScheduler() {
        assertThrows(NullPointerException.class, () -> {
            final SchedulerController schedulerController = new SchedulerController(null, authenticator);
        });
    }

    @Test
    void SchedulerController_nullIAuthenticator() {
        assertThrows(NullPointerException.class, () -> {
            final SchedulerController schedulerController = new SchedulerController(scheduler, null);
        });
    }

    @Test
    void getPrioritizedQueue_noError() {
        schedulerController.getPrioritizedQueue(pageable);

        verify(scheduler).getPrioritizedQueue(pageable);
    }

    @Test
    void getJobsQueue_noError() {
        schedulerController.getJobsQueue(pageable);

        verify(scheduler).getJobsQueue(pageable);
    }

    @Test
    void givePriorityTo_successfulAuthentication() {
        final String jwtToken = "jwt";

        when(authenticator.authenticate(jwtToken)).thenReturn(true);
        when(scheduler.givePriorityTo(JOB_GROUP, JOB_ID)).thenReturn(true);

        final boolean result = schedulerController.givePriorityTo(prioritizeMessage, jwtToken);

        verify(authenticator).authenticate(jwtToken);
        verify(scheduler).givePriorityTo(JOB_GROUP, JOB_ID);
        assertTrue(result);
    }

    @Test
    void givePriorityTo_unsuccessfulAuthentication() {
        final String jwtToken = "jwt";

        when(authenticator.authenticate(jwtToken)).thenReturn(false);
        when(scheduler.givePriorityTo(JOB_GROUP, JOB_ID)).thenReturn(true);

        final boolean result = schedulerController.givePriorityTo(prioritizeMessage, jwtToken);

        verify(authenticator).authenticate(jwtToken);
        verify(scheduler, never()).givePriorityTo(any(), any());
        assertFalse(result);
    }

    @Test
    void givePriorityTo_givePriorityFailed() {
        final String jwtToken = "jwt";

        when(authenticator.authenticate(jwtToken)).thenReturn(true);
        when(scheduler.givePriorityTo(JOB_GROUP, JOB_ID)).thenReturn(false);

        final boolean result = schedulerController.givePriorityTo(prioritizeMessage, jwtToken);

        verify(authenticator).authenticate(jwtToken);
        verify(scheduler).givePriorityTo(JOB_GROUP, JOB_ID);
        assertFalse(result);
    }

    @Test
    void givePriorityTo_invalidPrioritizeMessage() {
        final String jwtToken = "jwt";
        when(prioritizeMessage.validate()).thenReturn(false);

        final boolean result = schedulerController.givePriorityTo(prioritizeMessage, jwtToken);

        verify(authenticator, never()).authenticate(jwtToken);
        verify(scheduler, never()).givePriorityTo(JOB_GROUP, JOB_ID);
        assertFalse(result);
    }
}
