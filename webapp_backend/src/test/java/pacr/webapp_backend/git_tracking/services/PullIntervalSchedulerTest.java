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
    private PullIntervalSchedulingTrigger trigger;
    @Mock
    private TaskScheduler poolScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        scheduler = new PullIntervalScheduler(gitTracking, poolScheduler, trigger);
    }

    /**
     * Asserts that the trigger tasks are correct.
     */
    @Test
    void configureTasks() {
        scheduler.configureTasks(registrar);
        verify(registrar).setScheduler(poolScheduler);

        List<TriggerTask> triggerTaskList = registrar.getTriggerTaskList();
        assertEquals(1, triggerTaskList.size());
        TriggerTask task = triggerTaskList.get(0);

        assertEquals(trigger, task.getTrigger());
    }
}
