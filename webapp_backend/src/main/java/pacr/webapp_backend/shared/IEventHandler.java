package pacr.webapp_backend.shared;

/**
 * Allows to add new events to a category.
 */
public interface IEventHandler {

    /**
     * Adds a new event to the given category.
     *
     * @param category the category the event belongs to.
     * @param title the title of the event.
     * @param description a description of the event.
     */
    void addEvent(EventCategory category, String title, String description);

}
