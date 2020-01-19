package pacr.webapp_backend.event_management.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventHandlerTest {

    private static final String EVENT_TITLE = "eventTitle";
    private static final String EVENT_DESCRIPTION = "eventDescription";

    private EventCategory category;

    private EventHandler eventHandler;

    @Mock
    private IEventAccess eventAccess;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        category = EventCategory.BENCHMARKING;

        eventHandler = new EventHandler(eventAccess);
    }

    @Test
    void EventHandler_noError() {
        assertDoesNotThrow(() -> {
            EventHandler eventHandler = new EventHandler(eventAccess);
        });
    }

    @Test
    void EventHandler_nullIEventAccess() {
        assertThrows(NullPointerException.class, () -> {
            EventHandler eventHandler = new EventHandler(null);
        });
    }

    @Test
    void addEvent_noError() {
        LocalDateTime expectedCreated = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        eventHandler.addEvent(category, EVENT_TITLE, EVENT_DESCRIPTION);

        List<Event> events = eventHandler.getEvents(category);

        assertEquals(1, events.size());

        Event event = events.get(0);

        assertEquals(EVENT_TITLE, event.getTitle());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());
        assertEquals(expectedCreated, event.getCreated().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void addEvent_multipleCategories() {
        EventCategory category = EventCategory.BENCHMARKING;
        EventCategory otherCategory = EventCategory.LEADERBOARD;

        eventHandler.addEvent(category, EVENT_TITLE, EVENT_DESCRIPTION);
        eventHandler.addEvent(otherCategory, EVENT_TITLE, EVENT_DESCRIPTION);

        List<Event> categoryEvents = eventHandler.getEvents(category);
        assertEquals(1, categoryEvents.size());

        List<Event> otherCategoryEvents = eventHandler.getEvents(otherCategory);
        assertEquals(1, otherCategoryEvents.size());
    }

    @Test
    void addEvent_invalidCategory() {
        assertThrows(NullPointerException.class, () -> {
            eventHandler.addEvent(null, EVENT_TITLE, EVENT_DESCRIPTION);
        });
    }

    @Test
    void addEvent_invalidEventTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            eventHandler.addEvent(category, null, EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventHandler.addEvent(category, "", EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventHandler.addEvent(category, " ", EVENT_DESCRIPTION);
        });
    }

    @Test
    void addEvent_invalidEventDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            eventHandler.addEvent(category, EVENT_TITLE, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventHandler.addEvent(category, EVENT_TITLE, "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventHandler.addEvent(category, EVENT_TITLE, " ");
        });
    }

    @Test
    void getEvents_noEvents() {
        List<Event> events = eventHandler.getEvents(category);

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_nullCategory() {
        List<Event> events = eventHandler.getEvents(null);

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_noError() {
        List<Event> expectedEvents = new ArrayList<>();

        int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            expectedEvents.add(new Event(category, title, description));
            eventHandler.addEvent(category, title, description);
        }

        List<Event> events = eventHandler.getEvents(category);
        assertEquals(amtEvents, events.size());

        assertEquals(expectedEvents, events);
    }

    @Test
    void getEvents_wrongCategory() {
        EventCategory category = EventCategory.BENCHMARKING;
        EventCategory otherCategory = EventCategory.LEADERBOARD;

        int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            eventHandler.addEvent(category, title, description);
        }

        List<Event> events = eventHandler.getEvents(otherCategory);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_multipleCategories() {
        EventCategory category = EventCategory.BENCHMARKING;
        EventCategory otherCategory = EventCategory.LEADERBOARD;

        List<Event> expectedEvents = new ArrayList<>();

        int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            expectedEvents.add(new Event(category, title, description));
            eventHandler.addEvent(category, title, description);
        }

        int amtOtherEvents = 10;
        for (int i = 0; i < amtOtherEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            eventHandler.addEvent(otherCategory, title, description);
        }

        List<Event> events = eventHandler.getEvents(category);
        assertEquals(amtEvents, events.size());
    }
}
