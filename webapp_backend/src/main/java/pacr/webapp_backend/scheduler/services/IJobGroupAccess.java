package pacr.webapp_backend.scheduler.services;

/**
 * Provides access to stored JobGroups
 */
public interface IJobGroupAccess {

    /**
     * @return all stored JobGroups.
     */
    Iterable<JobGroup> findAllJobGroups();

    /**
     * Saves the given JobGroup.
     *
     * @param jobGroup the job group which is saved.
     */
    void saveJobGroup(JobGroup jobGroup);

}
