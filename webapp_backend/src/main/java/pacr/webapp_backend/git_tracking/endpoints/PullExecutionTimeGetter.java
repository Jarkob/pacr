package pacr.webapp_backend.git_tracking.endpoints;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.git_tracking.services.GitTracking;
import pacr.webapp_backend.git_tracking.services.NextExecutionGetter;

/**
 * Gets the next pull execution time.
 *
 * @author Pavel Zwerschke
 */
@RestController
public class PullExecutionTimeGetter {

    private final NextExecutionGetter nextExecutionGetter;
    private final GitTracking gitTracking;

    /**
     * Creates an instance of PullExecutionTimeGetter.
     * @param nextExecutionGetter is needed for getting the next execution time.
     * @param gitTracking is needed for checking if there is a pull happening or not.
     */
    public PullExecutionTimeGetter(final NextExecutionGetter nextExecutionGetter, final GitTracking gitTracking) {
        this.gitTracking = gitTracking;
        this.nextExecutionGetter = nextExecutionGetter;
    }

    /**
     * Returns the next execution time.
     * @return null if the system is currently pulling from a repository, else the next execution time.
     */
    @RequestMapping(path = "/next-execution-time")
    public ExecutionTime getNextExecutionTime() {
        return new ExecutionTime(gitTracking.isPullingFromAllRepositories(),
                nextExecutionGetter.getNextExecutionTime().toString());
    }

}
