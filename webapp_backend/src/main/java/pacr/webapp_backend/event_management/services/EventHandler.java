package pacr.webapp_backend.event_management.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.IEventHandler;

/**
 * Handles events belonging to multiple categories.
 */
@Component
public class EventHandler implements IEventHandler {

    private IEventAccess eventAccess;
    private Map<EventCategory, EventContainer> eventContainers;

    /**
     * Creates a new EventHandler.
     *
     * @param eventAccess the eventAccess used to save events.
     */
    public EventHandler(@NotNull IEventAccess eventAccess) {
        Objects.requireNonNull(eventAccess, "The eventAccess cannot be null.");

        this.eventAccess = eventAccess;
        this.eventContainers = new HashMap<>();
    }

    @Override
    public void addEvent(@NotNull EventCategory category, @NotNull String title, @NotNull String description) {
        verifyEventParameters(category, title, description);

        if (!eventContainers.containsKey(category)) {
            eventContainers.put(category, new EventContainer(category, eventAccess));
        }

        EventContainer eventContainer = eventContainers.get(category);
        eventContainer.addEvent(title, description);
    }

    private void verifyEventParameters(EventCategory category, String title, String description) {
        Objects.requireNonNull(category, "The category cannot be null.");

        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("The title cannot be null or empty.");
        }

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("The description cannot be null or empty.");
        }
    }

    /**
     * Returns all stored events that belong to the given category.
     *
     * @param category category of the returned events.
     * @return a list of events.
     */
    public List<Event> getEvents(EventCategory category) {
        if (eventContainers.containsKey(category)) {
            EventContainer eventContainer = eventContainers.get(category);

            return eventContainer.getEvents();
        }

        return new ArrayList<>();
    }
}
