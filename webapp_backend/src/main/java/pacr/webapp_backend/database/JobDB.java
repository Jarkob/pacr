package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import pacr.webapp_backend.scheduler.services.IJobAccess;
import pacr.webapp_backend.scheduler.services.Job;

/**
 * Implements the database access for the IJobAccess interface.
 */
public interface JobDB extends CrudRepository<Job, Integer>, IJobAccess {

    @Override
    default void saveJob(Job job) {
        this.save(job);
    }

    @Override
    default void deleteJob(Job job) {
        this.delete(job);
    }
}
