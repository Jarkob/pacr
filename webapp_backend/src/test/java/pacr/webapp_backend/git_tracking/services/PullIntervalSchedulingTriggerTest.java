package pacr.webapp_backend.git_tracking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests PullIntervalSchedulingTrigger.
 * Checks that the right date is outputted.
 *
 * @author Pavel Zwerschke
 */
public class PullIntervalSchedulingTriggerTest {

    private PullIntervalSchedulingTrigger trigger;

    int defaultInterval = 5;
    int intervalInDB = 20;
    @Mock
    private IPullIntervalAccess pullIntervalAccess;
    @Mock
    private TriggerContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(pullIntervalAccess.getPullInterval()).thenReturn(intervalInDB);
        trigger = new PullIntervalSchedulingTrigger(pullIntervalAccess, defaultInterval);
    }

    @Test
    void nextExecutionTime() {
        Date lastExecution = new Date();

        when(context.lastActualExecutionTime()).thenReturn(lastExecution);

        Date nextExecution = trigger.nextExecutionTime(context);

        assertNotNull(nextExecution);
        assertEquals(lastExecution.getTime() + intervalInDB * 1000, nextExecution.getTime());
    }

    @Test
    void nextExecutionTimeWithLastExecutionTimeNull() {
        when(context.lastActualExecutionTime()).thenReturn(null);

        assertNotNull(trigger.nextExecutionTime(context));
    }

    @Test
    void nextExecutionTimeWithLastExecutionTimeInstanceNull() {
        Date lastExecution = Mockito.mock(Date.class);
        when(context.lastActualExecutionTime()).thenReturn(lastExecution);
        when(lastExecution.toInstant()).thenReturn(null);

        assertNotNull(trigger.nextExecutionTime(context));
    }

    /**
     * Checks that the initialization of pull interval works correctly when the pull interval is not set in the DB.
     * Checks that PullIntervalScheduler tries to set the pull interval to the default value.
     */
    @Test
    void initializeWithPullIntervalNotSet() {
        when(pullIntervalAccess.getPullInterval()).thenThrow(NoSuchElementException.class).thenReturn(defaultInterval);

        trigger = new PullIntervalSchedulingTrigger(pullIntervalAccess, defaultInterval);
        verify(pullIntervalAccess).setPullInterval(defaultInterval);

        // check that nextExecutionTime is set correctly
        Date lastExecution = new Date();

        when(context.lastActualExecutionTime()).thenReturn(lastExecution);

        Date nextExecution = trigger.nextExecutionTime(context);

        assertNotNull(nextExecution);
        assertEquals(lastExecution.getTime() + defaultInterval * 1000, nextExecution.getTime());
    }
}
