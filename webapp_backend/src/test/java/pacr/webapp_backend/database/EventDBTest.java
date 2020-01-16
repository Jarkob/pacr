package pacr.webapp_backend.database;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.event_management.services.Event;
import pacr.webapp_backend.event_management.services.EventHandler;
import pacr.webapp_backend.event_management.services.IEventAccess;
import pacr.webapp_backend.shared.EventCategory;

@SpringBootTest
public class EventDBTest {

    private IEventAccess eventAccess;

    @Autowired
    public EventDBTest(IEventAccess access) {
        this.eventAccess = access;
    }

    @Test
    void test() {
        EventHandler handler = new EventHandler(eventAccess);

        handler.addEvent(EventCategory.LEADERBOARD, "testTitle", "testDesc");

        List<Event> events = eventAccess.findByCategory(EventCategory.LEADERBOARD);
    }
}
