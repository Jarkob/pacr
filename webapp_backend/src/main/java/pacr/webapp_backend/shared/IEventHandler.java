package pacr.webapp_backend.shared;

/**
 * Allows to add new events to the system.
 */
public interface IEventHandler {

    /**
     * Adds a new event that is created with the given template.
     *
     * @param eventTemplate the eventTemplate used to create the event.
     */
    void addEvent(EventTemplate eventTemplate);

}
