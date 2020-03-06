package pacr.webapp_backend.event_management.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private Pageable pageable;

    @Mock
    private Page<Event> expectedPage;

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
            final EventHandler eventHandler = new EventHandler(eventAccess);
        });
    }

    @Test
    void EventHandler_nullIEventAccess() {
        assertThrows(NullPointerException.class, () -> {
            final EventHandler eventHandler = new EventHandler(null);
        });
    }

    @Test
    void addEvent_noError() {
        final LocalDateTime expectedCreated = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        eventHandler.addEvent(eventTemplate);

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(List.of(
                new Event(category, EVENT_TITLE, EVENT_DESCRIPTION)));

        final List<Event> events = eventHandler.getEvents(category);

        assertEquals(1, events.size());

        final Event event = events.get(0);

        assertEquals(EVENT_TITLE, event.getTitle());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());
        assertEquals(expectedCreated, event.getCreated().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void addEvent_multipleCategories() {
        EventCategory otherCategory = EventCategory.UNDEFINED;
        final EventCategory category = EventCategory.BENCHMARKING;

        final EventTemplate otherTemplate = createEventTemplate(otherCategory, EVENT_TITLE, EVENT_DESCRIPTION);

        eventHandler.addEvent(eventTemplate);
        when(eventAccess.findByCategoryOrderByCreatedDesc(eventTemplate.getCategory())).thenReturn(List.of(
            new Event(category, EVENT_TITLE, EVENT_DESCRIPTION)));

        eventHandler.addEvent(otherTemplate);
        when(eventAccess.findByCategoryOrderByCreatedDesc(otherTemplate.getCategory())).thenReturn(List.of(
                new Event(otherCategory, EVENT_TITLE, EVENT_DESCRIPTION)));

        final List<Event> categoryEvents = eventHandler.getEvents(category);
        assertEquals(1, categoryEvents.size());

        final List<Event> otherCategoryEvents = eventHandler.getEvents(otherCategory);
        assertEquals(1, otherCategoryEvents.size());
    }

    @Test
    void getEvents_noEvents() {
        final List<Event> events = eventHandler.getEvents(category);

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_nullCategory() {
        final List<Event> events = eventHandler.getEvents(null);

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_noError() {
        final List<Event> expectedEvents = new ArrayList<>();

        final int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            expectedEvents.add(new Event(category, title, description));

            final EventTemplate template = createEventTemplate(category, title, description);
            eventHandler.addEvent(template);
        }

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(expectedEvents);

        final List<Event> events = eventHandler.getEvents(category);
        assertEquals(amtEvents, events.size());

        assertEquals(expectedEvents, events);
    }

    @Test
    void getEvents_wrongCategory() {
        EventCategory otherCategory = EventCategory.UNDEFINED;
        final EventCategory category = EventCategory.BENCHMARKING;

        final int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            final EventTemplate template = createEventTemplate(category, title, description);
            eventHandler.addEvent(template);
        }

        final List<Event> events = eventHandler.getEvents(otherCategory);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_multipleCategories() {
        EventCategory otherCategory = EventCategory.UNDEFINED;
        final EventCategory category = EventCategory.BENCHMARKING;

        final List<Event> expectedEvents = new ArrayList<>();

        final int amtEvents = 10;
        for (int i = 0; i < amtEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            expectedEvents.add(new Event(category, title, description));

            final EventTemplate template = createEventTemplate(category, title, description);
            eventHandler.addEvent(template);
        }

        final int amtOtherEvents = 10;
        for (int i = 0; i < amtOtherEvents; i++) {
            final String title = EVENT_TITLE + i;
            final String description = EVENT_DESCRIPTION + i;

            final EventTemplate template = createEventTemplate(otherCategory, title, description);
            eventHandler.addEvent(template);
        }

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(expectedEvents);

        final List<Event> events = eventHandler.getEvents(category);
        assertEquals(amtEvents, events.size());
        assertEquals(expectedEvents, events);
    }

    @Test
    void getEvents_pageable_unknownCategory() {
        Page<Event> page = eventHandler.getEvents(pageable, category);

        assertNotNull(page);
        assertEquals(0, page.getTotalElements());
        assertEquals(1, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    @Test
    void getEvents_pageable_noError() {
        eventHandler.addEvent(eventTemplate);

        when(eventAccess.findByCategory(pageable, category)).thenReturn(expectedPage);

        Page<Event> page = eventHandler.getEvents(pageable, category);

        assertEquals(expectedPage, page);
    }

    private EventTemplate createEventTemplate(EventCategory category, String title, String description) {
        EventTemplate template = mock(EventTemplate.class);

        when(template.getCategory()).thenReturn(category);
        when(template.getTitle()).thenReturn(title);
        when(template.getDescription()).thenReturn(description);

        return template;
    }
}
