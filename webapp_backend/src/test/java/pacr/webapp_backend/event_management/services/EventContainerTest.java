package pacr.webapp_backend.event_management.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventContainerTest {

    private static final String EVENT_TITLE = "eventTitle";
    private static final String EVENT_DESCRIPTION = "eventDescription";

    private EventContainer eventContainer;

    @Mock
    private IEventAccess eventAccess;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<Event> expectedPage;

    private EventCategory category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        this.category = EventCategory.BENCHMARKING;

        this.eventContainer = new EventContainer(category, eventAccess);
    }

    @Test
    void EventContainer_noError() {
        assertDoesNotThrow(() -> {
            EventContainer container = new EventContainer(category, eventAccess);
        });
    }

    @Test
    void EventContainer_nullCategory() {
        assertThrows(NullPointerException.class, () -> {
            EventContainer container = new EventContainer(null, eventAccess);
        });
    }

    @Test
    void EventContainer_nullIEventAccess() {
        assertThrows(NullPointerException.class, () -> {
            EventContainer container = new EventContainer(category, null);
        });
    }

    @Test
    void addEvent_noError() {
        LocalDateTime expectedCreated = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(List.of(new Event(category, EVENT_TITLE, EVENT_DESCRIPTION)));
        eventContainer.addEvent(EVENT_TITLE, EVENT_DESCRIPTION);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventAccess).saveEvent(captor.capture());

        Event event = captor.getValue();

        assertEvent(event, expectedCreated);

        List<Event> events = eventContainer.getEvents();
        assertEquals(1, events.size());

        assertEvent(events.get(0), expectedCreated);
    }

    @Test
    void addEvent_invalidTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            eventContainer.addEvent(null, EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventContainer.addEvent("", EVENT_DESCRIPTION);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventContainer.addEvent(" ", EVENT_DESCRIPTION);
        });
    }

    @Test
    void addEvent_invalidDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            eventContainer.addEvent(EVENT_TITLE, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventContainer.addEvent(EVENT_TITLE, "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            eventContainer.addEvent(EVENT_TITLE, " ");
        });
    }

    @Test
    void getEvents_noError() throws InterruptedException {
        int amtEvents = 5;
        List<Event> expectedEvents = new ArrayList<>();
        for (int i = 0; i < amtEvents; i++) {
            eventContainer.addEvent(EVENT_TITLE + i, EVENT_DESCRIPTION + i);
            expectedEvents.add(new Event(category, EVENT_TITLE + i, EVENT_DESCRIPTION + i));
            Thread.sleep(200);
        }

        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(expectedEvents);
        List<Event> events = eventContainer.getEvents();

        assertEquals(amtEvents, events.size());

        for (int i = 1; i < events.size(); i++) {
            for (int j = 0; j < i; j++) {
                Event eventAtJ = events.get(j);
                Event eventAtI = events.get(i);

                assertTrue(eventAtJ.getCreated().isBefore(eventAtI.getCreated()));
            }
        }
    }

    @Test
    void getEvents_noEventsAdded() {
        List<Event> events = eventContainer.getEvents();

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents_fetchesEvents() {
        // define new category so the method findByCategory(category) is only called once
        EventCategory category = EventCategory.UNDEFINED;

        List<Event> expectedEvents = new ArrayList<>();

        int amtEvents = 20;
        for (int i = 0; i < amtEvents; i++) {
            expectedEvents.add(new Event(category, EVENT_TITLE + i, EVENT_DESCRIPTION + i));
        }

        List<Event> sorted = expectedEvents;
        Collections.sort(sorted);
        when(eventAccess.findByCategoryOrderByCreatedDesc(category)).thenReturn(sorted);

        EventContainer eventContainer = new EventContainer(category, eventAccess);

        List<Event> events = eventContainer.getEvents();

        assertEquals(expectedEvents, events);
    }

    @Test
    void getEvents_pageable() {
        when(eventAccess.findByCategory(pageable, category)).thenReturn(expectedPage);

        Page<Event> page = eventContainer.getEvents(pageable);

        assertEquals(expectedPage, page);
    }

    @Test
    void getEvents_pageable_dbReturnsNull() {
        when(eventAccess.findByCategory(pageable, category)).thenReturn(null);

        Page<Event> page = eventContainer.getEvents(pageable);

        assertNotNull(page);
        assertEquals(0, page.getTotalElements());
        assertEquals(1, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    private void assertEvent(Event event, LocalDateTime expectedCreated) {
        assertEquals(EVENT_TITLE, event.getTitle());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());

        LocalDateTime created = event.getCreated().truncatedTo(ChronoUnit.SECONDS);

        assertEquals(expectedCreated, created);
    }
}
