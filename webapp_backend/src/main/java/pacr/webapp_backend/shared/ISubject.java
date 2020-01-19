package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;

public interface ISubject {

    /**
     * Registers a new observer to the subject.
     * @param observer the observer to be added.
     */
    void subscribe(@NotNull IObserver observer);

    /**
     * Unregisters an observer from the subject.
     * @param observer the observer to be removed.
     */
    void unsubscribe(@NotNull IObserver observer);

    /**
     * Notifies all registered observers.
     */
    void updateAll();

}
