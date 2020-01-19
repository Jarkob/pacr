package pacr.webapp_backend.scheduler.endpoints;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.scheduler.services.Scheduler;
import pacr.webapp_backend.shared.IAuthenticator;

/**
 * RestController for the Scheduler Component.
 */
@RestController
public class SchedulerController {

    private Scheduler scheduler;
    private IAuthenticator authenticator;

    /**
     * Creates a new SchedulerController
     * @param scheduler the scheduler which is used to handle the requests.
     * @param authenticator the authenticator which provides authentication services for secure methods.
     */
    public SchedulerController(@NotNull Scheduler scheduler, @NotNull IAuthenticator authenticator) {
        Objects.requireNonNull(scheduler, "The scheduler cannot be null.");
        Objects.requireNonNull(authenticator, "The authenticator cannot be null.");

        this.scheduler = scheduler;
        this.authenticator = authenticator;
    }

    /**
     * @return a list of all jobs and prioritized jobs currently in the scheduler.
     */
    @RequestMapping("/queue")
    public FullJobQueue getQueue() {
        return new FullJobQueue(scheduler.getPrioritizedQueue(), scheduler.getJobsQueue());
    }

    /**
     * Prioritizes the given job. This method is a secure method.
     * @param groupTitle the title of the job's group.
     * @param jobID the id of the job.
     * @param token a jwt token which is checked before executing the method.
     * @return if the given job was successfully prioritized.
     */
    @RequestMapping("/prioritize/{groupTitle}/{jobID}")
    public boolean givePriorityTo(
            @NotNull @PathVariable String groupTitle, @NotNull @PathVariable String jobID,
            @NotNull @RequestHeader(name = "jwt") String token) {

        Objects.requireNonNull(token, "The token cannot be null.");

        if (!StringUtils.hasText(groupTitle)) {
            throw new IllegalArgumentException("The groupTitle cannot be null or empty.");
        }

        if (!StringUtils.hasText(jobID)) {
            throw new IllegalArgumentException("The jobID cannot be null or empty.");
        }

        if (authenticator.authenticate(token)) {
            return scheduler.givePriorityTo(groupTitle, jobID);
        }

        return false;
    }

}
