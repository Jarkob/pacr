package pacr.webapp_backend.event_management.services;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import pacr.webapp_backend.shared.EventCategory;

/**
 * Represents an event with a title and a description.
 */
@Entity
public class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    private String title;
    private String description;
    private LocalDateTime created;

    /**
     * Creates an empty Event.
     *
     * Needed for JPA to work.
     */
    public Event() {
    }

    /**
     * Creates a new Event.
     *
     * @param category the category of the event.
     * @param title the title of the event.
     * @param description a description of the event.
     */
    Event(EventCategory category, String title, String description) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.created = LocalDateTime.now();
    }

    /**
     * @return the title of the event.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return a description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the date the event was created.
     */
    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public int compareTo(Event event) {
        assert (created != null);

        return created.compareTo(event.created);
    }
}
