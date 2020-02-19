package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.EventTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewResultEventTest {

    private static final String HASH = "12345678";
    private static final String REPO = "tichy ticker";
    private static final String NO_ERROR = null;
    private static final String ERROR = "could not open file";
    private static final int IMPROVEMENT = 24;
    private static final int HASH_LENGTH = 7;
    private static final String COMPARISON_HASH = "012345678";
    private static final String NO_COMPARISON = null;

    private static final String EXPECTED_NORMAL_TITLE = "'" +  HASH.substring(0, HASH_LENGTH)
            + "' Benchmarked for Repository '" + REPO + "'";
    private static final String EXPECTED_NORMAL_DESCRIPTION = "On average, the result is "
            + IMPROVEMENT + "% better than the previous one ('"
            + COMPARISON_HASH.substring(0, HASH_LENGTH) + "').";
    private static final String EXPECTED_ERROR_TITLE = "Error While Benchmarking '" + HASH.substring(0, HASH_LENGTH)
            + "' for Repository '" + REPO + "'";
    private static final String EXPECTED_ERROR_DESCRIPTION = "Error message: '" + ERROR + "'";
    private static final String EXPECTED_NO_COMPARISON_DESCRIPTION = "No data was found for comparison.";

    /**
     * Tests if title and description get generated properly if it is a successful result with comparison data.
     */
    @Test
    void getTitleGetDescription_successfulBenchmark_shouldReturnNormalTitle() {
        EventTemplate eventTemplate = new NewResultEvent(EventCategory.BENCHMARKING, HASH, REPO, NO_ERROR, IMPROVEMENT,
                COMPARISON_HASH);

        String title = eventTemplate.getTitle();
        String description = eventTemplate.getDescription();

        System.out.println(title);
        System.out.println(description);

        assertEquals(EXPECTED_NORMAL_TITLE, title);
        assertEquals(EXPECTED_NORMAL_DESCRIPTION, description);
    }

    /**
     * Tests if title and description get generated properly if it is an unsuccessful result (with global error).
     */
    @Test
    void getTitleGetDescription_errorBenchmark_shouldReturnErrorTitle() {
        EventTemplate eventTemplate = new NewResultEvent(EventCategory.BENCHMARKING, HASH, REPO, ERROR, IMPROVEMENT,
                COMPARISON_HASH);

        String title = eventTemplate.getTitle();
        String description = eventTemplate.getDescription();

        System.out.println(title);
        System.out.println(description);

        assertEquals(EXPECTED_ERROR_TITLE, title);
        assertEquals(EXPECTED_ERROR_DESCRIPTION, description);
    }

    /**
     * Tests if title and description get generated properly if it is a successful result without comparison data.
     */
    @Test
    void getTitleGetDescription_noComparison_shouldReturnDescriptionWithoutComparison() {
        EventTemplate eventTemplate = new NewResultEvent(EventCategory.BENCHMARKING, HASH, REPO, NO_ERROR, IMPROVEMENT,
                NO_COMPARISON);

        String title = eventTemplate.getTitle();
        String description = eventTemplate.getDescription();

        System.out.println(title);
        System.out.println(description);

        assertEquals(EXPECTED_NORMAL_TITLE, title);
        assertEquals(EXPECTED_NO_COMPARISON_DESCRIPTION, description);
    }

    /**
     * Tests if title and description get generated properly if it is an unsuccessful result (with global error)
     * without comparison data. Should behave the same as if there was comparison data because unsuccessful results can
     * never be compared anyways.
     */
    @Test
    void getTitleGetDescription_errorAndNoComparison_shouldReturnErrorTitle() {
        EventTemplate eventTemplate = new NewResultEvent(EventCategory.BENCHMARKING, HASH, REPO, ERROR, IMPROVEMENT,
                NO_COMPARISON);

        String title = eventTemplate.getTitle();
        String description = eventTemplate.getDescription();

        System.out.println(title);
        System.out.println(description);

        assertEquals(EXPECTED_ERROR_TITLE, title);
        assertEquals(EXPECTED_ERROR_DESCRIPTION, description);
    }
}
