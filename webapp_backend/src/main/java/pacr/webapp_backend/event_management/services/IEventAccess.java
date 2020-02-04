package pacr.webapp_backend.event_management.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Finds a page of events belonging to the given category.
     *
     * @param pageable information about the requested page.
     * @param category the category all found events must belong to.
     * @return a list of events.
     */
    Page<Event> findByCategory(Pageable pageable, EventCategory category);

    /**
     * Finds all events belonging to the given category.
     *
     * @param category the category all found events must belong to.
     * @return a list of events.
     */
    List<Event> findByCategoryOrderByCreatedDesc(EventCategory category);

}
