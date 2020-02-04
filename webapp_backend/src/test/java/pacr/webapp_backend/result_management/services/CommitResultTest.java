package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pacr.webapp_backend.result_management.services.ResultControllerTest.REPO_ID;

public class CommitResultTest {

    private static final int MAX_STRING_LENGTH = 2000;

    /**
     * Tests whether the constructor of a CommitResult properly truncates an error message that is too long.
     */
    @Test
    void constructor_errorTooLong_shouldTruncateError() {
        String errorTooLong = "0".repeat(MAX_STRING_LENGTH * 2);
        SimpleBenchmarkingResult result = new SimpleBenchmarkingResult();
        result.setGlobalError(errorTooLong);

        CommitResult commitResult = new CommitResult(result, new HashSet<>(), REPO_ID, LocalDateTime.now(), null);

        assertEquals(MAX_STRING_LENGTH, commitResult.getGlobalError().length());
        assertEquals(errorTooLong.substring(0, MAX_STRING_LENGTH), commitResult.getGlobalError());
    }
}