package pacr.webapp_backend.scheduler.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.FieldSetter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

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

    private void setupJob(Job job, String jobId, JobGroup group) {
        setField(job, "jobID", jobId);
        setField(job, "group", group);
    }

    private void setField(Object object, String fieldName, Object value) {
        try {
            FieldSetter.setField(object, object.getClass().getDeclaredField(fieldName), value);
        } catch (NoSuchFieldException e) {
            fail();
        }
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
    void equals_and_hashCode_noError() {
        EqualsVerifier.forClass(Job.class)
                .withOnlyTheseFields("jobID", "group")
                .verify();
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
