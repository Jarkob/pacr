package pacr.webapp_backend.shared;

import java.util.Collection;
import java.util.Set;

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

    /**
     * Removes the given set of jobs ids belonging to the group.
     *
     * @param groupTitle the title of the group.
     * @param jobIDs a set of jobIDs.
     */
    void removeJobs(@NotNull String groupTitle, @NotNull Set<String> jobIDs);

}
