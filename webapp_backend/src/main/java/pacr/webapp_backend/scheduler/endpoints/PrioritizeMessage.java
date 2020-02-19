package pacr.webapp_backend.scheduler.endpoints;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * Represents a message from the frontend to prioritize a job.
 */
@Getter
@NoArgsConstructor
public class PrioritizeMessage {

    private String jobID;
    private String groupTitle;

    /**
     * @return whether all attributes are valid inputs.
     */
    public boolean validate() {
        return StringUtils.hasText(groupTitle) && StringUtils.hasText(jobID);
    }
}
