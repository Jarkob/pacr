package pacr.webapp_backend.event_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.*;

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
        Event compareEvent1 = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);
        Event compareEvent2 = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);

        assertEquals(compareEvent1, compareEvent2);
    }

    @Test
    void hashCode_noError() {
        Event compareEvent1 = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);
        Event compareEvent2 = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);

        long hashCode1 = compareEvent1.hashCode();
        long hashCode2 = compareEvent2.hashCode();

        assertEquals(hashCode1, hashCode2);
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
