package pacr.webapp_backend.event_management.endpoints;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.servlet.View;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.EventHandler;
import pacr.webapp_backend.shared.EventCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class EventManagementControllerTest {

    private EventManagementController eventController;

    @Mock
    private List<Event> expectedBenchmarkingEvents;

    @Mock
    private List<Event> expectedLeaderboardEvents;

    @Mock
    private EventHandler eventHandler;

    @Mock
    private Page<Event> leaderboardPage;

    @Mock
    private Page<Event> benchmarkingPage;

    @Mock
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        eventController = new EventManagementController(eventHandler);

        when(leaderboardPage.getContent()).thenReturn(expectedLeaderboardEvents);
        when(benchmarkingPage.getContent()).thenReturn(expectedBenchmarkingEvents);

        when(eventHandler.getEvents(pageable, EventCategory.LEADERBOARD)).thenReturn(leaderboardPage);
        when(eventHandler.getEvents(pageable, EventCategory.BENCHMARKING)).thenReturn(benchmarkingPage);
    }

    @Test
    void getBenchmarkingEvents_noError() {
        List<Event> events = eventController.getBenchmarkingEvents(pageable).getContent();

        assertEquals(expectedBenchmarkingEvents, events);
    }

    @Test
    void getLeaderboardEvents_noError() {
        Page<Event> page = eventController.getLeaderboardEvents(pageable);
        List<Event> events = page.getContent();

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
