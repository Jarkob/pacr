package pacr.webapp_backend.git_tracking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Pavel Zwerschke
 */
public class PullIntervalSchedulerTest {

    private PullIntervalScheduler scheduler;

    @Mock
    private GitTracking gitTracking;
    @Spy
    private ScheduledTaskRegistrar registrar;
    @Mock
    private IPullIntervalAccess pullIntervalAccess;
    @Mock
    private TaskScheduler poolScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        scheduler = new PullIntervalScheduler(gitTracking, poolScheduler, pullIntervalAccess, 30);
    }

    /**
     * Asserts that the trigger tasks are correct.
     */
    @Test
    void configureTasks() {
        scheduler.configureTasks(registrar);
        verify(registrar).setScheduler(poolScheduler);

        verify(poolScheduler).scheduleWithFixedDelay(any(), any(), anyLong());
    }
}
