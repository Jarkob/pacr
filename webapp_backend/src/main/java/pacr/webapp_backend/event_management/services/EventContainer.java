package pacr.webapp_backend.event_management.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.EventCategory;

/**
 * Holds a list of events belonging to the same category and saves them through the eventAccess.
 */
public class EventContainer {

    private IEventAccess eventAccess;

    private EventCategory category;
    private List<Event> events;

    /**
     * Creates a new event manager that handles events for the given category.
     *
     * @param category the category of events handles by this event manager.
     * @param eventAccess the event access used to store the events.
     */
    EventContainer(@NotNull EventCategory category, @NotNull IEventAccess eventAccess) {
        Objects.requireNonNull(category, "The category cannot be null.");
        Objects.requireNonNull(eventAccess, "The eventAccess cannot be null.");

        this.category = category;
        this.eventAccess = eventAccess;

        // fetch all events that are available with this category
        this.events = new ArrayList<>(eventAccess.findByCategory(category));
    }

    /**
     * Adds a new event to the event manager.
     *
     * @param title the title of the event.
     * @param description a description of the event.
     */
    void addEvent(@NotNull String title, @NotNull String description) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("title cannot be null or empty.");
        }

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("description cannot be null or empty.");
        }

        Event event = new Event(category, title, description);

        eventAccess.saveEvent(event);
        events.add(event);
    }

    /**
     * @return a sorted list of all events in this event manager.
     */
    List<Event> getEvents() {
        List<Event> toReturn = new ArrayList<>(events);

        Collections.sort(toReturn);

        return toReturn;
    }
}
