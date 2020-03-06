package pacr.webapp_backend.scheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AdvancedSchedulingAlgorithmTest {

    private AdvancedSchedulingAlgorithm schedulingAlgorithm;

    private static final long jobGroup1TimeSheet = 5;
    private static final long jobGroup2TimeSheet = 15;

    // job2Queued is after job1Queued
    private static final LocalDateTime job1Queued = LocalDateTime.of(2020, 10, 1, 0, 0, 0);
    private static final LocalDateTime job2Queued = LocalDateTime.of(2021, 10, 1, 0, 0, 0);

    @Mock
    private Job job1;

    @Mock
    private Job job2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(job1.getGroupTimeSheet()).thenReturn(jobGroup1TimeSheet);
        when(job2.getGroupTimeSheet()).thenReturn(jobGroup2TimeSheet);

        when(job1.getQueued()).thenReturn(job1Queued);
        when(job2.getQueued()).thenReturn(job2Queued);

        this.schedulingAlgorithm = new AdvancedSchedulingAlgorithm();
    }

    @Test
    void compare_differentTimeSheet() {
        int result = schedulingAlgorithm.compare(job1, job2);
        assertEquals(-1, result);

        result = schedulingAlgorithm.compare(job2, job1);
        assertEquals(1, result);
    }

    @Test
    void compare_sameTimeSheet() {
        when(job2.getGroupTimeSheet()).thenReturn(jobGroup1TimeSheet);

        int result = schedulingAlgorithm.compare(job1, job2);
        assertEquals(1, result);

        result = schedulingAlgorithm.compare(job2, job1);
        assertEquals(-1, result);
    }

    @Test
    void compare_null() {
        int result = schedulingAlgorithm.compare(null, job2);
        assertEquals(1, result);

        result = schedulingAlgorithm.compare(job1, null);
        assertEquals(-1, result);
    }
}
