package pacr.webapp_backend.event_management.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.EventCategory;

import javax.validation.constraints.NotNull;

/**
 * Holds a list of events belonging to the same category and saves them through the eventAccess.
 */
public class EventContainer {

    private final IEventAccess eventAccess;

    private final EventCategory category;

    /**
     * Creates a new event manager that handles events for the given category.
     *
     * @param category the category of events handles by this event manager.
     * @param eventAccess the event access used to store the events.
     */
    EventContainer(@NotNull final EventCategory category, @NotNull final IEventAccess eventAccess) {
        Objects.requireNonNull(category, "The category cannot be null.");
        Objects.requireNonNull(eventAccess, "The eventAccess cannot be null.");

        this.category = category;
        this.eventAccess = eventAccess;
    }

    /**
     * Adds a new event to the event manager.
     *
     * @param title the title of the event.
     * @param description a description of the event.
     */
    void addEvent(@NotNull final String title, @NotNull final String description) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("title cannot be null or empty.");
        }

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("description cannot be null or empty.");
        }

        final Event event = new Event(category, title, description);

        eventAccess.saveEvent(event);
    }

    /**
     * @param pageable information about the requested page.
     * @return a sorted list of all events in this event manager.
     */
    Page<Event> getEvents(Pageable pageable) {
        Page<Event> page = eventAccess.findByCategory(pageable, category);

        if (page == null) {
            page = new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        return page;
    }

    List<Event> getEvents() {
        return eventAccess.findByCategoryOrderByCreatedDesc(category);
    }
}
