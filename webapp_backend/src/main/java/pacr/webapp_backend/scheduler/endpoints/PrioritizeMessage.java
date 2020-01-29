package pacr.webapp_backend.scheduler.endpoints;

import org.springframework.util.StringUtils;

/**
 * Represents a message from the frontend to prioritize a job.
 */
public class PrioritizeMessage {

    private String jobID;
    private String groupTitle;

    /**
     * Creates an empty PrioritizeMessage.
     *
     * Needed for Spring.
     */
    public PrioritizeMessage() {
    }

    /**
     * @return the jobID of the job which is going to be prioritized.
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * @return the group title the job belongs to.
     */
    public String getGroupTitle() {
        return groupTitle;
    }

    public boolean validate() {
        return StringUtils.hasText(groupTitle) && StringUtils.hasText(jobID);
    }
}
