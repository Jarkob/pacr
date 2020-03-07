package pacr.webapp_backend.scheduler.endpoints;

import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.scheduler.services.Job;
import pacr.webapp_backend.scheduler.services.Scheduler;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;

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
     * Gets a subset of prioritized and normal jobs from the queue.
     *
     * @param pageable provides information about which page to load.
     * @return a list of all jobs and prioritized jobs currently in the scheduler.
     */
    @RequestMapping("/queue/prioritized")
    public Page<Job> getPrioritizedQueue(@PageableDefault(size = 15) Pageable pageable) {
        return scheduler.getPrioritizedQueue(pageable);
    }

    /**
     * Gets a subset of prioritized and normal jobs from the queue.
     *
     * @param pageable provides information about which page to load.
     * @return a list of all jobs and prioritized jobs currently in the scheduler.
     */
    @RequestMapping("/queue/jobs")
    public Page<Job> getJobsQueue(@PageableDefault(size = 5) Pageable pageable) {
        return scheduler.getJobsQueue(pageable);
    }

    /**
     * Prioritizes the given job. This method is a secure method.
     * @param prioritizeMessage the message containing the relevant data to prioritize a job.
     * @param token a jwt token which is checked before executing the method.
     * @return if the given job was successfully prioritized.
     */
    @PostMapping("/prioritize")
    public boolean givePriorityTo(
            @NotNull @RequestBody PrioritizeMessage prioritizeMessage,
            @NotNull @RequestHeader(name = "jwt") String token) {

        Objects.requireNonNull(token, "The token cannot be null.");
        Objects.requireNonNull(prioritizeMessage, "The prioritize message cannot be null.");

        if (!prioritizeMessage.validate()) {
            return false;
        }

        if (authenticator.authenticate(token)) {
            return scheduler.givePriorityTo(prioritizeMessage.getGroupTitle(), prioritizeMessage.getJobID());
        }

        return false;
    }

}
