package pacr.webapp_backend.shared;

import java.util.Collection;
import javax.validation.constraints.NotNull;

/**
 * Allows the scheduling of jobs.
 */
public interface IJobScheduler {

    /**
     * Adds all given jobIDs as new jobs. The new jobs are associated with the given group.
     *
     * @param groupTitle the title of the group.
     * @param jobIDs a list of job ids.
     */
    void addJobs(@NotNull String groupTitle, @NotNull Collection<String> jobIDs);

    /**
     * Removes all jobs belonging to the given job group.
     *
     * @param groupTitle the title of the group.
     */
    void removeJobGroup(@NotNull String groupTitle);

}
