package pacr.webapp_backend.scheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JobGroupTest {

    private final String GROUP_TITLE = "groupTitle";
    private JobGroup jobGroup;

    @BeforeEach
    void initialize() {
        jobGroup = new JobGroup(GROUP_TITLE);
    }

    @Test
    void equals_areEqual() {
        JobGroup otherGroup = new JobGroup(GROUP_TITLE);

        boolean result = otherGroup.equals(jobGroup);
        assertTrue(result);

        result = jobGroup.equals(jobGroup);
        assertTrue(result);
    }

    @Test
    void equals_areNotEqual() {
        JobGroup otherGroup = new JobGroup(GROUP_TITLE + 1);

        boolean result = otherGroup.equals(jobGroup);
        assertFalse(result);

        result = jobGroup.equals(null);
        assertFalse(result);

        result = jobGroup.equals(new Object());
        assertFalse(result);
    }

    @Test
    void JobGroup_noError() {
        assertDoesNotThrow(() -> {
            JobGroup jobGroup = new JobGroup(GROUP_TITLE);
        });
    }

    @Test
    void JobGroup_invalidTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            JobGroup jobGroup = new JobGroup(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            JobGroup jobGroup = new JobGroup("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            JobGroup jobGroup = new JobGroup(" ");
        });
    }

    @Test
    void JobGroup_correctTitle() {
        JobGroup jobGroup = new JobGroup(GROUP_TITLE);

        assertEquals(GROUP_TITLE, jobGroup.getTitle());
    }

    @Test
    void JobGroup_timeSheetIsZero() {
        JobGroup jobGroup = new JobGroup(GROUP_TITLE);

        assertEquals(0, jobGroup.getTimeSheet());
    }

    @Test
    void updateTimeSheet_noError() {
        final long TIME = 13;

        jobGroup.addToTimeSheet(TIME);

        assertEquals(TIME, jobGroup.getTimeSheet());
    }

    @Test
    void updateTimeSheet_addZero_noError() {
        final long TIME = 0;

        jobGroup.addToTimeSheet(TIME);

        assertEquals(TIME, jobGroup.getTimeSheet());
    }

    @Test
    void updateTimeSheet_multiple_noError() {
        final long TIME = 13;
        final int ITERATIONS = 4;
        final long expected = ITERATIONS * TIME;

        for (int i = 0; i < ITERATIONS; i++) {
            jobGroup.addToTimeSheet(TIME);
        }

        assertEquals(expected, jobGroup.getTimeSheet());
    }

    @Test
    void updateTimeSheet_negative() {
        final long TIME = -13;

        assertThrows(IllegalArgumentException.class, () -> {
            jobGroup.addToTimeSheet(TIME);
        });
    }

    @Test
    void resetBenchmarkingTime_noError() {
        final long TIME = 20;

        jobGroup.addToTimeSheet(TIME);

        jobGroup.resetTimeSheet();

        assertEquals(0, jobGroup.getTimeSheet());
    }
}
