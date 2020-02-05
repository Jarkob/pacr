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

    /**
     * Deletes a job group from the storage.
     *
     * @param jobGroup the group which is removed.
     */
    void deleteGroup(JobGroup jobGroup);

}
