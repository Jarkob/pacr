package pacr.webapp_backend.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.IEventAccess;

/**
 * Provides the database access for events.
 */
@Component
public interface EventDB extends PagingAndSortingRepository<Event, Integer>, IEventAccess {

    @Override
    default void saveEvent(Event event) {
        this.save(event);
    }

}
