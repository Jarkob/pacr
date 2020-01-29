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
}
