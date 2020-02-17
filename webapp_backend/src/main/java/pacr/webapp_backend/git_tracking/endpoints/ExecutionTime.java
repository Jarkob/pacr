package pacr.webapp_backend.git_tracking.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Represents a execution time.
 *
 * @author Pavel Zwerschke
 */
@AllArgsConstructor @Getter
public class ExecutionTime {

    private boolean pulling;
    private String nextExecutionTime;

}
