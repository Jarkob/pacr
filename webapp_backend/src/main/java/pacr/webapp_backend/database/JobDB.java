package pacr.webapp_backend.database;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import pacr.webapp_backend.scheduler.services.IJobAccess;
import pacr.webapp_backend.scheduler.services.Job;

/**
 * Implements the database access for the IJobAccess interface.
 */
public interface JobDB extends PagingAndSortingRepository<Job, Integer>, IJobAccess {

    @Override
    default void saveJob(final Job job) {
        this.save(job);
    }

    @Override
    default void saveJobs(final Collection<Job> jobs) {
        this.saveAll(jobs);
    }

    @Override
    default void deleteJob(final Job job) {
        this.delete(job);
    }

    @Override
    default void deleteJobs(final Collection<Job> jobs) {
        this.deleteAll(jobs);
    }

    @Override
    default Page<Job> findJobs(final Pageable pageable) {
        return findAllByPrioritizedOrderByQueuedDesc(false, pageable);
    }

    @Override
    default List<Job> findJobs() {
        return findAllByPrioritizedOrderByQueuedDesc(false);
    }

    @Override
    default Page<Job> findPrioritized(final Pageable pageable) {
        return findAllByPrioritizedOrderByQueuedAsc(true, pageable);
    }

    @Override
    default List<Job> findPrioritized() {
        return findAllByPrioritizedOrderByQueuedAsc(true);
    }

    @Override
    default Collection<Job> findAllJobs(final String groupTitle) {
        return findAllByGroup_Title(groupTitle);
    }

    Collection<Job> findAllByGroup_Title(String groupTitle);

    Page<Job> findAllByPrioritizedOrderByQueuedDesc(boolean prioritized, Pageable pageable);

    Page<Job> findAllByPrioritizedOrderByQueuedAsc(boolean prioritized, Pageable pageable);

    List<Job> findAllByPrioritizedOrderByQueuedAsc(boolean prioritized);

    List<Job> findAllByPrioritizedOrderByQueuedDesc(boolean prioritized);
}
