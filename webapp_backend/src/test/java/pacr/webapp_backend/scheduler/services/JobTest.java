package pacr.webapp_backend.scheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class JobTest {

    private static final String JOB_ID = "jobID";
    private static final String GROUP_TITLE = "groupTitle";

    private Job job;

    private JobGroup jobGroup;

    @BeforeEach
    void setUp() {
        jobGroup = new JobGroup(GROUP_TITLE);

        job = new Job(JOB_ID, jobGroup);
    }

    @Test
    void Job_noArgs() {
        assertDoesNotThrow(() -> {
            Job job = new Job();
        });
    }

    @Test
    void Job_withArgs() {
        assertDoesNotThrow(() -> {
            Job job = new Job(JOB_ID, jobGroup);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Job job = new Job("", jobGroup);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Job job = new Job(" ", jobGroup);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Job job = new Job(null, jobGroup);
        });

        assertThrows(NullPointerException.class, () -> {
            Job job = new Job(JOB_ID, null);
        });
    }

    @Test
    void equals_noError() {
        JobGroup jobGroup2 = new JobGroup(GROUP_TITLE);
        Job job2 = new Job(JOB_ID, jobGroup2);
        assertEquals(job, job2);

        assertNotEquals(job, new Object());
        assertNotEquals(job, null);
        assertEquals(job, job);
    }

    @Test
    void hashCode_noError() {
        JobGroup jobGroup2 = new JobGroup(GROUP_TITLE);
        Job job2 = new Job(JOB_ID, jobGroup2);
        assertEquals(job.hashCode(), job2.hashCode());
    }

    @Test
    void setPrioritized_noError() {
        assertDoesNotThrow(() -> {
            job.setPrioritized(true);
        });
    }

    @Test
    void getGroupTimeSheet_noError() {
        final long expectedTimeSheet = 5;
        jobGroup.addToTimeSheet(expectedTimeSheet);

        long timeSheet = job.getGroupTimeSheet();

        assertEquals(expectedTimeSheet, timeSheet);
    }

    @Test
    void getJobGroupTitle_noError() {
        String jobGroupTitle = job.getJobGroupTitle();

        assertEquals(GROUP_TITLE, jobGroupTitle);
    }

    @Test
    void getGroup_noError() {
        JobGroup group = job.getGroup();

        assertEquals(jobGroup, group);
    }

    @Test
    void getJobID_noError() {
        String jobId = job.getJobID();

        assertEquals(JOB_ID, jobId);
    }

    @Test
    void getQueued_noError() {
        LocalDateTime expectedQueued = LocalDateTime.now();
        Job job = new Job(JOB_ID, jobGroup);

        assertEquals(expectedQueued.truncatedTo(ChronoUnit.SECONDS), job.getQueued().truncatedTo(ChronoUnit.SECONDS));
    }
}
