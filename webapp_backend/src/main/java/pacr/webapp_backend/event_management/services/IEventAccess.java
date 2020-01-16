package pacr.webapp_backend.event_management.services;

import java.util.List;
<<<<<<< Updated upstream
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.EventCategory;

@Component
=======
import pacr.webapp_backend.shared.EventCategory;

>>>>>>> Stashed changes
public interface IEventAccess {

    void saveEvent(Event event);

    List<Event> findByCategory(EventCategory category);

}
