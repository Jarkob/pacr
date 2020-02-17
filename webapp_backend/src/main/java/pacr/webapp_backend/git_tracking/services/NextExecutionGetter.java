package pacr.webapp_backend.git_tracking.services;

import java.time.LocalDateTime;

/**
 * Gets the next pull execution time.
 *
 * @author Pavel Zwerschke
 */
public interface NextExecutionGetter {

    /**
     * @return the next time a pull is being executed.
     */
    LocalDateTime getNextExecutionTime();

}
