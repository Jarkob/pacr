package pacr.webapp_backend.event_management.services;

import java.util.List;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.EventCategory;

/**
 * Interace to store and retrieve events.
 */
public interface IEventAccess {

    /**
     * Saves or updates an event.
     *
     * @param event the event to be saved.
     */
    void saveEvent(Event event);

    /**
     * Finds all events belonging to the given category.
     *
     * @param category the category all found events must belong to.
     * @return a list of events.
     */
    List<Event> findByCategory(EventCategory category);

}
