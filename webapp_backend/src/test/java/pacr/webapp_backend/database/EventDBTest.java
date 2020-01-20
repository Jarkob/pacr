package pacr.webapp_backend.database;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class EventDBTest {

    private static final String EVENT_TITLE = "eventTitle";
    private static final String EVENT_DESCRIPTION = "eventDescription";

    private EventCategory category;

    private EventDB eventDB;

    @Autowired
    public EventDBTest(EventDB eventDB) {
        MockitoAnnotations.initMocks(this);

        this.eventDB = eventDB;
        this.category = EventCategory.BENCHMARKING;

        this.eventDB.deleteAll();
    }

    @AfterEach
    void setUp() {
        this.eventDB.deleteAll();
    }

    @Test
    void saveEvent_noError() {
        LocalDateTime expectedCreated = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Event expectedEvent = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);

        eventDB.saveEvent(expectedEvent);

        List<Event> events = eventDB.findByCategory(category);
        assertEquals(1, events.size());

        Event event = events.get(0);

        assertEquals(EVENT_TITLE, event.getTitle());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());

        Duration delta = Duration.between(expectedCreated, event.getCreated());
        assertTrue(delta.toMillis() < 1500);
    }

    @Test
    void findByCategory_multipleCategories() {
        EventCategory category = EventCategory.BENCHMARKING;
        EventCategory otherCategory = EventCategory.LEADERBOARD;

        LocalDateTime expectedCreated = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Event expectedEvent = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);

        Event otherEvent = new Event(otherCategory, EVENT_TITLE, EVENT_DESCRIPTION);

        eventDB.saveEvent(expectedEvent);
        eventDB.saveEvent(otherEvent);

        List<Event> events = eventDB.findByCategory(category);
        assertEquals(1, events.size());

        Event event = events.get(0);

        assertEquals(EVENT_TITLE, event.getTitle());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());

        Duration delta = Duration.between(expectedCreated, event.getCreated());
        assertTrue(delta.toMillis() < 1500);
    }
}
