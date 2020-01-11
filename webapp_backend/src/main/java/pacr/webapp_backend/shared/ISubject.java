package pacr.webapp_backend.shared;

public interface ISubject {

    /**
     * Registers a new observer to the subject.
     * @param observer the observer to be added.
     */
    void subscribe(IObserver observer);

    /**
     * Unregisters an observer from the subject.
     * @param observer the observer to be removed.
     */
    void unsubscribe(IObserver observer);

    /**
     * Notifies all registered observers.
     */
    void updateAll();

}
