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
    private static final int PRIORITIZED_PAGE_SIZE = 15;
    private static final int JOB_PAGE_SIZE = 5;

    private final Scheduler scheduler;
    private final IAuthenticator authenticator;

    /**
     * Creates a new SchedulerController
     * @param scheduler the scheduler which is used to handle the requests.
     * @param authenticator the authenticator which provides authentication services for secure methods.
     */
    public SchedulerController(@NotNull final Scheduler scheduler, @NotNull final IAuthenticator authenticator) {
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
    public Page<Job> getPrioritizedQueue(@PageableDefault(size = PRIORITIZED_PAGE_SIZE) final Pageable pageable) {
        return scheduler.getPrioritizedQueue(pageable);
    }

    /**
     * Gets a subset of prioritized and normal jobs from the queue.
     *
     * @param pageable provides information about which page to load.
     * @return a list of all jobs and prioritized jobs currently in the scheduler.
     */
    @RequestMapping("/queue/jobs")
    public Page<Job> getJobsQueue(@PageableDefault(size = JOB_PAGE_SIZE) final Pageable pageable) {
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
            @NotNull @RequestBody final PrioritizeMessage prioritizeMessage,
            @NotNull @RequestHeader(name = "jwt") final String token) {

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
