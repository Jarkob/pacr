package pacr.webapp_backend.event_management.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.EventTemplate;
import pacr.webapp_backend.shared.IEventHandler;

/**
 * Handles events belonging to multiple categories.
 * Events with a title and a description are supported.
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
    public void addEvent(@NotNull EventTemplate eventTemplate) {
        Objects.requireNonNull(eventTemplate, "The eventTemplate cannot be null.");

        EventCategory category = eventTemplate.getCategory();

        if (!eventContainers.containsKey(category)) {
            EventContainer eventContainer = new EventContainer(category, eventAccess);
            eventContainers.put(category, eventContainer);
        }

        EventContainer eventContainer = eventContainers.get(category);
        eventContainer.addEvent(eventTemplate.getTitle(), eventTemplate.getDescription());
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
