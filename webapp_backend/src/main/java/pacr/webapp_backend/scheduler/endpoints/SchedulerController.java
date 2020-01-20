package pacr.webapp_backend.scheduler.endpoints;

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
    public SchedulerController(Scheduler scheduler, IAuthenticator authenticator) {
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
            @PathVariable String groupTitle, @PathVariable String jobID, @RequestHeader(name = "jwt") String token) {

        if (authenticator.authenticate(token)) {
            return scheduler.givePriorityTo(groupTitle, jobID);
        }

        return false;
    }

}
