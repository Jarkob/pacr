package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.EventTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewResultEventTest {

    private static final String HASH = "ads803";
    private static final String REPO = "tichy ticker";
    private static final String NO_ERROR = null;
    private static final String ERROR = "could not open file";
    private static final int IMPROVEMENT = 24;
    private static final String COMPARISON_HASH = "das089d";
    private static final String NO_COMPARISON = null;

    private static final String EXPECTED_NORMAL_TITLE = "New Benchmarking Result for Repository '" + REPO + "'";
    private static final String EXPECTED_NORMAL_DESCRIPTION = "A new benchmarking result was measured for the commit '"
            + HASH + "' from repository '" + REPO + "'. On average, the new benchmarking result is "
            + IMPROVEMENT + " percent better then the previous one (commit '" + COMPARISON_HASH + "').";
    private static final String EXPECTED_ERROR_TITLE = "Error While Benchmarking Commit for Repository '" + REPO + "'";
    private static final String EXPECTED_ERROR_DESCRIPTION = "An error occurred while benchmarking commit '"
            + HASH + "' for repository '" + REPO + "': '" + ERROR + "'";
    private static final String EXPECTED_NO_COMPARISON_DESCRIPTION = "A new benchmarking result was measured for the commit '"
            + HASH + "' from repository '" + REPO + "'. No data was found for comparison.";

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

    @Test
    void getTitleGetDescription_errorAndNoComparsion_shouldReturnErrorTitle() {
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
