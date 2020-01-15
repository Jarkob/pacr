package pacr.webapp_backend.event_management.services;

import java.util.List;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.EventCategory;

@Component
public interface IEventAccess {

    void saveEvent(Event event);

    List<Event> findByCategory(EventCategory category);

}
