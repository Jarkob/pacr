package pacr.webapp_backend.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.IEventAccess;

@Component
public interface EventDB extends JpaRepository<Event, Integer>, IEventAccess {

    @Override
    default void saveEvent(Event event) {
        this.save(event);
    }

}
