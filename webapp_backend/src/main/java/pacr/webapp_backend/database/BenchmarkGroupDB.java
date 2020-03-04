package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.services.BenchmarkGroup;
import pacr.webapp_backend.result_management.services.IBenchmarkGroupAccess;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a default implementation of the IBenchmarkGroupAccess interface.
 */
@Component
public interface BenchmarkGroupDB extends CrudRepository<BenchmarkGroup, Integer>, IBenchmarkGroupAccess {

    @Override
    default Collection<BenchmarkGroup> getAllGroups() {
        final List<BenchmarkGroup> groups = new LinkedList<>();
        this.findAll().forEach(groups::add);
        return groups;
    }

    @Override
    default BenchmarkGroup getBenchmarkGroup(final int id) {
        return this.findById(id).orElse(null);
    }

    @Override
    default int saveBenchmarkGroup(final BenchmarkGroup group) {
        this.save(group);
        return group.getId();
    }

    @Override
    default void deleteGroup(final BenchmarkGroup group) {
        this.delete(group);
    }

    @Override
    default BenchmarkGroup getStandardGroup() {
        return findBenchmarkGroupByStandardGroup(true);
    }

    /**
     * @param standardGroup defines whether to look for standard group or other groups
     * @return the standard group (if standardGroup is {@code true}) or any other group
     */
    BenchmarkGroup findBenchmarkGroupByStandardGroup(boolean standardGroup);
}
