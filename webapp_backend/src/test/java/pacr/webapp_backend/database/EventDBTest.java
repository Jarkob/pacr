package pacr.webapp_backend.database;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventDBTest extends SpringBootTestWithoutShell {

    private static final String EVENT_TITLE = "eventTitle";
    private static final String EVENT_DESCRIPTION = "eventDescription";

    private EventCategory category;

    private EventDB eventDB;

    Pageable pageable;

    @Autowired
    public EventDBTest(final EventDB eventDB) {
        MockitoAnnotations.initMocks(this);

        this.eventDB = eventDB;
        this.category = EventCategory.BENCHMARKING;

        this.eventDB.deleteAll();
    }

    @AfterEach
    void setUp() {
        this.eventDB.deleteAll();

        this.pageable = Pageable.unpaged();
    }

    @Test
    void saveEvent_noError() {
        final LocalDateTime expectedCreated = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final Event expectedEvent = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);

        eventDB.saveEvent(expectedEvent);

        final List<Event> events = eventDB.findByCategory(pageable, category).getContent();
        assertEquals(1, events.size());

        final Event event = events.get(0);

        assertEquals(EVENT_TITLE, event.getTitle());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());

        final Duration delta = Duration.between(expectedCreated, event.getCreated());
        assertTrue(delta.toMillis() < 1500);
    }

    @Test
    void findByCategory_multipleCategories() {
        EventCategory category = EventCategory.BENCHMARKING;
        EventCategory otherCategory = EventCategory.UNDEFINED;

        final LocalDateTime expectedCreated = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final Event expectedEvent = new Event(category, EVENT_TITLE, EVENT_DESCRIPTION);

        final Event otherEvent = new Event(otherCategory, EVENT_TITLE, EVENT_DESCRIPTION);

        eventDB.saveEvent(expectedEvent);
        eventDB.saveEvent(otherEvent);

        final List<Event> events = eventDB.findByCategory(pageable, category).getContent();
        assertEquals(1, events.size());

        final Event event = events.get(0);

        assertEquals(EVENT_TITLE, event.getTitle());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());

        final Duration delta = Duration.between(expectedCreated, event.getCreated());
        assertTrue(delta.toMillis() < 1500);
    }
}
