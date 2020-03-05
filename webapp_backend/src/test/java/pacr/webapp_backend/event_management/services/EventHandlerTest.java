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
import pacr.webapp_backend.shared.EventTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventHandlerTest {

    private static final String EVENT_TITLE = "eventTitle";
    private static final String EVENT_DESCRIPTION = "eventDescription";

    private EventCategory category;

    private EventHandler eventHandler;

    private EventTemplate eventTemplate;

    @Mock
    private IEventAccess eventAccess;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        category = EventCategory.BENCHMARKING;

        eventHandler = new EventHandler(eventAccess);

        eventTemplate = createEventTemplate(category, EVENT_TITLE, EVENT_DESCRIPTION);
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
        eventHandler.addEvent(eventTemplate);

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(List.of(
                new Event(category, EVENT_TITLE, EVENT_DESCRIPTION)));

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
        EventCategory otherCategory = EventCategory.UNDEFINED;

        EventTemplate otherTemplate = createEventTemplate(otherCategory, EVENT_TITLE, EVENT_DESCRIPTION);

        eventHandler.addEvent(eventTemplate);
        when(eventAccess.findByCategoryOrderByCreatedDesc(eventTemplate.getCategory())).thenReturn(List.of(
            new Event(category, EVENT_TITLE, EVENT_DESCRIPTION)));

        eventHandler.addEvent(otherTemplate);
        when(eventAccess.findByCategoryOrderByCreatedDesc(otherTemplate.getCategory())).thenReturn(List.of(
                new Event(otherCategory, EVENT_TITLE, EVENT_DESCRIPTION)));

        List<Event> categoryEvents = eventHandler.getEvents(category);
        assertEquals(1, categoryEvents.size());

        List<Event> otherCategoryEvents = eventHandler.getEvents(otherCategory);
        assertEquals(1, otherCategoryEvents.size());
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

            EventTemplate template = createEventTemplate(category, title, description);
            eventHandler.addEvent(template);
        }

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(expectedEvents);

        List<Event> events = eventHandler.getEvents(category);
        assertEquals(amtEvents, events.size());

        assertEquals(expectedEvents, events);
    }

    @Test
    void getEvents_wrongCategory() {
        EventCategory category = EventCategory.BENCHMARKING;
        EventCategory otherCategory = EventCategory.UNDEFINED;

        int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            EventTemplate template = createEventTemplate(category, title, description);
            eventHandler.addEvent(template);
        }

        List<Event> events = eventHandler.getEvents(otherCategory);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_multipleCategories() {
        EventCategory category = EventCategory.BENCHMARKING;
        EventCategory otherCategory = EventCategory.UNDEFINED;

        List<Event> expectedEvents = new ArrayList<>();

        int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            expectedEvents.add(new Event(category, title, description));

            EventTemplate template = createEventTemplate(category, title, description);
            eventHandler.addEvent(template);
        }

        int amtOtherEvents = 10;
        for (int i = 0; i < amtOtherEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            EventTemplate template = createEventTemplate(otherCategory, title, description);
            eventHandler.addEvent(template);
        }

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(expectedEvents);

        List<Event> events = eventHandler.getEvents(category);
        assertEquals(amtEvents, events.size());
        assertEquals(expectedEvents, events);
    }

    private EventTemplate createEventTemplate(EventCategory category, String title, String description) {
        EventTemplate template = mock(EventTemplate.class);

        when(template.getCategory()).thenReturn(category);
        when(template.getTitle()).thenReturn(title);
        when(template.getDescription()).thenReturn(description);

        return template;
    }
}
