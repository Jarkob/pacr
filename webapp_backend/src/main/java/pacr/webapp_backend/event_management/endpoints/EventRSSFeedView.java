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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.EventHandler;
import pacr.webapp_backend.shared.EventCategory;

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
    public EventRSSFeedView(@NotNull EventCategory category, @NotNull EventHandler eventHandler) {
        Objects.requireNonNull(category, "The category cannot be null.");
        Objects.requireNonNull(eventHandler, "The eventHandler cannot be null.");

        this.category = category;
        this.eventHandler = eventHandler;
    }

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
        final String title = "PACR RSS Feed";
        final String description = "Here you can find all events that occurred in the system.";
        final String link = "http://localhost:8080/rss";

        feed.setTitle(title);
        feed.setDescription(description);
        feed.setLink(link);
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> map, HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse) {

        List<Event> events = eventHandler.getEvents(category);

        List<Item> items = new ArrayList<>();

        for (Event event : events) {
            Item item = new Item();

            item.setTitle(event.getTitle());

            Date pubDate = Date.from(event.getCreated().atZone(ZoneId.systemDefault()).toInstant());
            item.setPubDate(pubDate);

            Content content = new Content();
            content.setType("text/plain");
            content.setValue(event.getDescription());
            item.setContent(content);

            items.add(item);
        }

        return items;
    }
}
