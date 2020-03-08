package pacr.webapp_backend.event_management.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.EventTemplate;
import pacr.webapp_backend.shared.IEventHandler;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

/**
 * Handles events belonging to multiple categories.
 * Events with a title and a description are supported.
 */
@Component
public class EventHandler implements IEventHandler {

    private final IEventAccess eventAccess;
    private final Map<EventCategory, EventContainer> eventContainers;

    /**
     * Creates a new EventHandler.
     *
     * @param eventAccess the eventAccess used to save events.
     */
    public EventHandler(@NotNull final IEventAccess eventAccess) {
        Objects.requireNonNull(eventAccess, "The eventAccess cannot be null.");

        this.eventAccess = eventAccess;
        this.eventContainers = new HashMap<>();
    }

    @Override
    public void addEvent(@NotNull final EventTemplate eventTemplate) {
        Objects.requireNonNull(eventTemplate, "The eventTemplate cannot be null.");

        final EventCategory category = eventTemplate.getCategory();

        initializeEventContainer(category);

        EventContainer eventContainer = eventContainers.get(category);
        eventContainer.addEvent(eventTemplate.getTitle(), eventTemplate.getDescription());
    }

    @PostConstruct
    private void initialize() {
        for (EventCategory category : EventCategory.values()) {
            eventContainers.put(category, new EventContainer(category, eventAccess));
        }
    }

    private void initializeEventContainer(EventCategory category) {
        if (!eventContainers.containsKey(category)) {
            final EventContainer eventContainer = new EventContainer(category, eventAccess);
            eventContainers.put(category, eventContainer);
        }
    }

    /**
     * Returns all stored events that belong to the given category.
     *
     * @param pageable information about the requested page.
     * @param category category of the returned events.
     * @return a list of events.
     */
    public Page<Event> getEvents(final Pageable pageable, final EventCategory category) {
        if (eventContainers.containsKey(category)) {
            final EventContainer eventContainer = eventContainers.get(category);

            return eventContainer.getEvents(pageable);
        }

        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Returns all stored events that belong to the given category.
     *
     * @param category category of the returned events.
     * @return a list of events.
     */
    public List<Event> getEvents(final EventCategory category) {
        if (eventContainers.containsKey(category)) {
            final EventContainer eventContainer = eventContainers.get(category);

            return eventContainer.getEvents();
        }

        return new ArrayList<>();
    }
}
