package pacr.webapp_backend.scheduler.endpoints;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.scheduler.services.Job;
import pacr.webapp_backend.scheduler.services.Scheduler;
import pacr.webapp_backend.shared.IAuthenticator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        this.schedulerController = new SchedulerController(scheduler, authenticator);
    }

    @Test
    void SchedulerController_noError() {
        assertDoesNotThrow(() -> {
            SchedulerController schedulerController = new SchedulerController(scheduler, authenticator);
        });
    }

    @Test
    void SchedulerController_nullScheduler() {
        assertThrows(NullPointerException.class, () -> {
            SchedulerController schedulerController = new SchedulerController(null, authenticator);
        });
    }

    @Test
    void SchedulerController_nullIAuthenticator() {
        assertThrows(NullPointerException.class, () -> {
            SchedulerController schedulerController = new SchedulerController(scheduler, null);
        });
    }

    @Test
    void getQueue_noError() {
        List<Job> jobs = new ArrayList<>();
        Job job = mock(Job.class);
        jobs.add(job);

        List<Job> prioritized = new ArrayList<>();
        Job prioritizedJob = mock(Job.class);
        prioritized.add(prioritizedJob);

        when(scheduler.getJobsQueue()).thenReturn(jobs);
        when(scheduler.getPrioritizedQueue()).thenReturn(prioritized);

        FullJobQueue jobQueue = schedulerController.getQueue();

        assertEquals(jobs, jobQueue.getJobs());
        assertEquals(prioritized, jobQueue.getPrioritizedJobs());
    }

    @Test
    void givePriorityTo_successfulAuthentication() {
        final String jwtToken = "jwt";

        when(authenticator.authenticate(jwtToken)).thenReturn(true);
        when(scheduler.givePriorityTo(JOB_GROUP, JOB_ID)).thenReturn(true);

        boolean result = schedulerController.givePriorityTo(JOB_GROUP, JOB_ID, jwtToken);

        verify(authenticator).authenticate(jwtToken);
        verify(scheduler).givePriorityTo(JOB_GROUP, JOB_ID);
        assertTrue(result);
    }

    @Test
    void givePriorityTo_unsuccessfulAuthentication() {
        final String jwtToken = "jwt";

        when(authenticator.authenticate(jwtToken)).thenReturn(false);
        when(scheduler.givePriorityTo(JOB_GROUP, JOB_ID)).thenReturn(true);

        boolean result = schedulerController.givePriorityTo(JOB_GROUP, JOB_ID, jwtToken);

        verify(authenticator).authenticate(jwtToken);
        verify(scheduler, never()).givePriorityTo(any(), any());
        assertFalse(result);
    }

    @Test
    void givePriorityTo_givePriorityFailed() {
        final String jwtToken = "jwt";

        when(authenticator.authenticate(jwtToken)).thenReturn(true);
        when(scheduler.givePriorityTo(JOB_GROUP, JOB_ID)).thenReturn(false);

        boolean result = schedulerController.givePriorityTo(JOB_GROUP, JOB_ID, jwtToken);

        verify(authenticator).authenticate(jwtToken);
        verify(scheduler).givePriorityTo(JOB_GROUP, JOB_ID);
        assertFalse(result);
    }
}
