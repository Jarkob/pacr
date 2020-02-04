package pacr.webapp_backend.event_management.endpoints;

import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.EventHandler;
import pacr.webapp_backend.shared.EventCategory;

/**
 * Provides a REST interface to access leaderboard and benchmarking events as lists or RSS-Feeds.
 */
@RestController
public class EventManagementController {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private EventHandler eventHandler;

    private EventRSSFeedView benchmarkingRSSFeed;
    private EventRSSFeedView leaderboardRSSFeed;

    /**
     * Creates a new EventManagementController.
     *
     * @param eventHandler the eventHandler used to retrieve events.
     */
    public EventManagementController(@NotNull EventHandler eventHandler) {
        Objects.requireNonNull(eventHandler, "The eventHandler cannot be null.");

        this.eventHandler = eventHandler;

        this.leaderboardRSSFeed = new EventRSSFeedView(EventCategory.LEADERBOARD, eventHandler);
        this.benchmarkingRSSFeed = new EventRSSFeedView(EventCategory.BENCHMARKING, eventHandler);
    }

    /**
     * @param pageable information about the requested page
     * @return a list of all leaderboard events.
     */
    @RequestMapping("/events/leaderboard")
    public Page<Event> getLeaderboardEvents(@PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"created"}) Pageable pageable) {
        return eventHandler.getEvents(pageable, EventCategory.LEADERBOARD);
    }

    /**
     * @param pageable information about the requested page
     * @return a list of all benchmarking events.
     */
    @RequestMapping("/events/benchmark")
    public Page<Event> getBenchmarkingEvents(@PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"created"}) Pageable pageable) {
        return eventHandler.getEvents(pageable, EventCategory.BENCHMARKING);
    }

    /**
     * @return a RSS-Feed view of all benchmarking events.
     */
    @RequestMapping("/rss/benchmark")
    public View benchmarkingRSSFeed() {
        return benchmarkingRSSFeed;
    }

    /**
     * @return a RSS-Feed view of all benchmarking events.
     */
    @RequestMapping("/rss/leaderboard")
    public View leaderboardRSSFeed() {
        return leaderboardRSSFeed;
    }

}
