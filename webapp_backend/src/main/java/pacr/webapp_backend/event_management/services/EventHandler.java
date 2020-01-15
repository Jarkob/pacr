package pacr.webapp_backend.event_management.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.IEventHandler;

/**
 * Handles events from multiple categories.
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
    public EventHandler(IEventAccess eventAccess) {
        this.eventAccess = eventAccess;
        this.eventContainers = new HashMap<>();
    }

    @Override
    public void addEvent(EventCategory category, String title, String description) {
        verifyEventParameters(category, title, description);

        if (!eventContainers.containsKey(category)) {
            eventContainers.put(category, new EventContainer(category, eventAccess));
        }

        EventContainer eventContainer = eventContainers.get(category);
        eventContainer.addEvent(title, description);
    }

    private void verifyEventParameters(EventCategory category, String title, String description) {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null.");
        }

        if (stringIsNotValid(title)) {
            throw new IllegalArgumentException("title cannot be null or empty.");
        }

        if (stringIsNotValid(description)) {
            throw new IllegalArgumentException("description cannot be null or empty.");
        }
    }

    private boolean stringIsNotValid(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }

    public List<Event> getEvents(EventCategory category) {
        if (eventContainers.containsKey(category)) {
            EventContainer eventContainer = eventContainers.get(category);

            return eventContainer.getEvents();
        }

        return new ArrayList<>();
    }
}
