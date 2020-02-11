package pacr.webapp_backend.database;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pacr.webapp_backend.scheduler.services.IJobAccess;
import pacr.webapp_backend.scheduler.services.Job;

/**
 * Implements the database access for the IJobAccess interface.
 */
public interface JobDB extends JpaRepository<Job, Integer>, IJobAccess {

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

    @Override
    default void deleteJobs(Collection<Job> jobs) {
        this.deleteAll(jobs);
    }

    @Override
    default List<Job> findJobs() {
        return findAllByPrioritizedOrderByQueuedDesc(false);
    }

    @Override
    default List<Job> findPrioritized() {
        return findAllByPrioritizedOrderByQueuedDesc(true);
    }

    @Override
    default Collection<Job> findJobs(String groupTitle) {
        return findAllByGroup_Title(groupTitle);
    }

    Collection<Job> findAllByGroup_Title(String groupTitle);

    List<Job> findAllByPrioritizedOrderByQueuedDesc(boolean prioritized);

}
