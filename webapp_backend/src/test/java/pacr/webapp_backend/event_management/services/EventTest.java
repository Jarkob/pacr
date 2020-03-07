package pacr.webapp_backend.event_management.services;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class EventTest {

    private static EventCategory category = EventCategory.BENCHMARKING;
    private static final String EVENT_TITLE = "eventTitle";
    private static final String EVENT_DESCRIPTION = "eventDescription";

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);
    }

    @Test
    void Event_noArgs_noException() {
        assertDoesNotThrow(() -> {
            Event event = new Event();
        });
    }

    @Test
    void Event_withArgs() {
        assertDoesNotThrow(() -> {
            Event event = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Event event = new Event(category, "", EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Event event = new Event(category, " ", EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Event event = new Event(category, null, EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Event event = new Event(category, EVENT_TITLE, "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Event event = new Event(category, EVENT_TITLE, " ");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Event event = new Event(category, EVENT_TITLE, null);
        });

        assertThrows(NullPointerException.class, () -> {
            Event event = new Event(null, EVENT_TITLE, EVENT_DESCRIPTION);
        });
    }

    @Test
    void equals_noError() {
        EqualsVerifier.forClass(Event.class)
                .withOnlyTheseFields("category", "title", "description", "created")
                .verify();
    }

    @Test
    void compareTo_noError() {
        Event event1 = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }
        Event event2 = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);

        int expectedResult = event1.getCreated().compareTo(event2.getCreated());
        int result = event1.compareTo(event2);

        assertEquals(expectedResult, result);
    }
}
