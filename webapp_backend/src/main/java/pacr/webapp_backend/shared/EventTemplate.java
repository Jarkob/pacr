package pacr.webapp_backend.shared;

import java.util.Objects;
import org.springframework.util.StringUtils;

/**
 * A template to create an event with a custom title and description.
 */
public abstract class EventTemplate {

    private static final String EMPTY_TITLE = "no title";
    private static final String EMPTY_DESCRIPTION = "no description";

    private final EventCategory category;

    /**
     * Creates a new EventTemplate to create events for a given category.
     *
     * @param category the category of the created events.
     */
    public EventTemplate(final EventCategory category) {
        Objects.requireNonNull(category, "The category cannot be null.");

        this.category = category;
    }

    /**
     * @return the category of the created events.
     */
    public final EventCategory getCategory() {
        return category;
    }

    /**
     * @return the title of the event.
     */
    public final String getTitle() {
        final String title = buildTitle();

        if (StringUtils.hasText(title)) {
            return title;
        }

        return EMPTY_TITLE;
    }

    /**
     * @return a custom title for the event.
     */
    protected abstract String buildTitle();

    /**
     * @return the description of the created events.
     */
    public final String getDescription() {
        final String description = buildDescription();

        if (StringUtils.hasText(description)) {
            return description;
        }

        return EMPTY_DESCRIPTION;
    }

    /**
     * @return a custom description of the event.
     */
    protected abstract String buildDescription();

}
