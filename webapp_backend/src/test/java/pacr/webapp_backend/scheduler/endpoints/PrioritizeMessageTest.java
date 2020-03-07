package pacr.webapp_backend.scheduler.endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.FieldSetter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PrioritizeMessageTest {

    private static final String JOB_ID = "jobID";
    private static final String GROUP_TITLE = "groupTitle";

    private PrioritizeMessage message;

    @BeforeEach
    void setUp() {
        this.message = new PrioritizeMessage();
    }

    private void setupMessage(String jobId, String groupTitle) {
        setField(message, "jobID", jobId);
        setField(message, "groupTitle", groupTitle);
    }

    private void setField(Object object, String fieldName, Object value) {
        try {
            FieldSetter.setField(object, object.getClass().getDeclaredField(fieldName), value);
        } catch (NoSuchFieldException e) {
            fail();
        }
    }

    @Test
    void validate_noError() {
        setupMessage(JOB_ID, GROUP_TITLE);
        assertTrue(message.validate());

        setupMessage(null, GROUP_TITLE);
        Assertions.assertFalse(message.validate());

        setupMessage("", GROUP_TITLE);
        Assertions.assertFalse(message.validate());

        setupMessage(" ", GROUP_TITLE);
        Assertions.assertFalse(message.validate());

        setupMessage(JOB_ID, null);
        Assertions.assertFalse(message.validate());

        setupMessage(JOB_ID, "");
        Assertions.assertFalse(message.validate());

        setupMessage(JOB_ID, " ");
        Assertions.assertFalse(message.validate());
    }
}
