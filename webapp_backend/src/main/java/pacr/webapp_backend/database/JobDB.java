package pacr.webapp_backend.database;

import java.util.Collection;
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
    default void saveJobs(Collection<Job> jobs) {
        this.saveAll(jobs);
    }

    @Override
    default void deleteJob(Job job) {
        this.delete(job);
    }
}
