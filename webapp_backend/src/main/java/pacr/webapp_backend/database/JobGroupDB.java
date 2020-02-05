package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import pacr.webapp_backend.scheduler.services.IJobGroupAccess;
import pacr.webapp_backend.scheduler.services.JobGroup;

/**
 * Implements the database access for the IJobGroupAccess.
 */
public interface JobGroupDB extends CrudRepository<JobGroup, Integer>, IJobGroupAccess {

    @Override
    default Iterable<JobGroup> findAllJobGroups() {
        return this.findAll();
    }

    @Override
    default void saveJobGroup(JobGroup jobGroup) {
        this.save(jobGroup);
    }

    @Override
    default void deleteGroup(JobGroup jobGroup) {
        this.delete(jobGroup);
    }
}
