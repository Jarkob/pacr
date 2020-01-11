package pacr.webapp_backend.scheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JobGroupTest {

    private final String GROUP_TITLE = "groupTitle";
    private JobGroup jobGroup;

    @BeforeEach
    void initialize() {
        jobGroup = new JobGroup(GROUP_TITLE);
    }

    @Test
    void createJobGroup_correctTitle() {
        assertEquals(GROUP_TITLE, jobGroup.getTitle());
    }

    @Test
    void createJobGroup_benchmarkingTime() {
        assertEquals(0, jobGroup.getBenchmarkingTime());
    }

    @Test
    void createJobGroup_nullTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            JobGroup group = new JobGroup(null);
        });
    }

    @Test
    void createJobGroup_emptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            JobGroup group = new JobGroup("");
        });
    }

    @Test
    void updateTimeSheet_noError() {
        final long TIME = 13;

        jobGroup.updateTimeSheet(TIME);

        assertEquals(TIME, jobGroup.getBenchmarkingTime());
    }

    @Test
    void updateTimeSheet_addZero_noError() {
        final long TIME = 0;

        jobGroup.updateTimeSheet(TIME);

        assertEquals(TIME, jobGroup.getBenchmarkingTime());
    }

    @Test
    void updateTimeSheet_multiple_noError() {
        final long TIME = 13;
        final int ITERATIONS = 4;
        final long expected = ITERATIONS * TIME;

        for (int i = 0; i < ITERATIONS; i++) {
            jobGroup.updateTimeSheet(TIME);
        }

        assertEquals(expected, jobGroup.getBenchmarkingTime());
    }

    @Test
    void updateTimeSheet_negative() {
        final long TIME = -13;

        assertThrows(IllegalArgumentException.class, () -> {
            jobGroup.updateTimeSheet(TIME);
        });
    }

}
