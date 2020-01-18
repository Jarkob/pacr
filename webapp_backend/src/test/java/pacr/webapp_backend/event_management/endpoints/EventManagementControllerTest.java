package pacr.webapp_backend.event_management.endpoints;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.View;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.EventHandler;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class EventManagementControllerTest {

    private EventManagementController eventController;

    @Mock
    private List<Event> expectedBenchmarkingEvents;

    @Mock
    private List<Event> expectedLeaderboardEvents;

    @Mock
    private EventHandler eventHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        eventController = new EventManagementController(eventHandler);

        when(eventHandler.getEvents(EventCategory.LEADERBOARD)).thenReturn(expectedLeaderboardEvents);
        when(eventHandler.getEvents(EventCategory.BENCHMARKING)).thenReturn(expectedBenchmarkingEvents);
    }

    @Test
    void getBenchmarkingEvents_noError() {
        List<Event> events = eventController.getBenchmarkingEvents();

        assertEquals(expectedBenchmarkingEvents, events);
    }

    @Test
    void getLeaderboardEvents_noError() {
        List<Event> events = eventController.getLeaderboardEvents();

        assertEquals(expectedLeaderboardEvents, events);
    }

    @Test
    void benchmarkingRSSFeed_noError() {
        View rssView = eventController.benchmarkingRSSFeed();

        assertNotNull(rssView);
        assertEquals("application/rss+xml", rssView.getContentType());
    }

    @Test
    void leaderboardRSSFeed_noError() {
        View rssView = eventController.leaderboardRSSFeed();

        assertNotNull(rssView);
        assertEquals("application/rss+xml", rssView.getContentType());
    }
}
