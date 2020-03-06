package pacr.webapp_backend.scheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GroupSchedulingAlgorithmTest {

    private static final long JOB_GROUP1_TIME = 5;
    private static final long JOB_GROUP2_TIME = 15;

    private GroupSchedulingAlgorithm schedulingAlgorithm;

    @Mock
    private JobGroup jobGroup1;

    @Mock
    private JobGroup jobGroup2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(jobGroup1.getTimeSheet()).thenReturn(JOB_GROUP1_TIME);
        when(jobGroup2.getTimeSheet()).thenReturn(JOB_GROUP2_TIME);

        this.schedulingAlgorithm = new GroupSchedulingAlgorithm();
    }

    @Test
    void compare_noError() {
        int result = schedulingAlgorithm.compare(jobGroup1, jobGroup2);
        assertEquals(-1, result);

        result = schedulingAlgorithm.compare(jobGroup2, jobGroup1);
        assertEquals(1, result);

        result = schedulingAlgorithm.compare(jobGroup1, jobGroup1);
        assertEquals(0, result);
    }

    @Test
    void compare_null() {
        int result = schedulingAlgorithm.compare(null, jobGroup2);
        assertEquals(1, result);

        result = schedulingAlgorithm.compare(jobGroup1, null);
        assertEquals(-1, result);
    }
}
