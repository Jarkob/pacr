package pacr.webapp_backend.event_management.endpoints;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Item;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.EventHandler;
import pacr.webapp_backend.shared.EventCategory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * RSS Feed View for one event category.
 */
public class EventRSSFeedView extends AbstractRssFeedView {

    private EventCategory category;
    private EventHandler eventHandler;

    /**
     * Creates a new EventRSSFeedView.
     *
     * @param category the category the displayed events belong to.
     * @param eventHandler the eventHandler used to retrieve the displayed events.
     */
    public EventRSSFeedView(@NotNull final EventCategory category, @NotNull final EventHandler eventHandler) {
        Objects.requireNonNull(category, "The category cannot be null.");
        Objects.requireNonNull(eventHandler, "The eventHandler cannot be null.");

        this.category = category;
        this.eventHandler = eventHandler;
    }

    @Override
    protected void buildFeedMetadata(final Map<String, Object> model, final Channel feed, final HttpServletRequest request) {
        final String title = "PACR RSS Feed";
        final String description = "Here you can find all events that occurred in the system.";
        final String link = "http://localhost:8080/rss";

        feed.setTitle(title);
        feed.setDescription(description);
        feed.setLink(link);
    }

    @Override
    protected List<Item> buildFeedItems(final Map<String, Object> map, final HttpServletRequest httpServletRequest,
                                        final HttpServletResponse httpServletResponse) {

        final List<Event> events = eventHandler.getEvents(category);

        final List<Item> items = new ArrayList<>();

        for (final Event event : events) {
            final Item item = new Item();

            item.setTitle(event.getTitle());

            final Date pubDate = Date.from(event.getCreated().atZone(ZoneId.systemDefault()).toInstant());
            item.setPubDate(pubDate);

            final Content content = new Content();
            content.setType("text/plain");
            content.setValue(event.getDescription());
            item.setContent(content);

            items.add(item);
        }

        return items;
    }
}
