package pacr.webapp_backend.event_management.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.EventCategory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Represents an event with a title and a description.
 */
@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Event implements Comparable<Event> {
    private static final int MAX_EVENT_AMOUNT = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private int id;

    @Enumerated(EnumType.STRING)
    @Getter(AccessLevel.NONE)
    private EventCategory category;

    @Column(length = MAX_EVENT_AMOUNT)
    private String title;

    @Column(length = MAX_EVENT_AMOUNT)
    private String description;

    private LocalDateTime created;

    /**
     * Creates a new Event.
     *
     * @param category the category of the event.
     * @param title the title of the event.
     * @param description a description of the event.
     */
    public Event(@NotNull final EventCategory category, @NotNull final String title,
                 @NotNull final String description) {
        Objects.requireNonNull(category, "The category cannot be null.");

        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("The title cannot be null or empty.");
        }

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("The description cannot be null or empty.");
        }

        this.category = category;
        this.title = title;
        this.description = description;
        this.created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public int compareTo(final Event event) {
        return created.compareTo(event.created);
    }

}
