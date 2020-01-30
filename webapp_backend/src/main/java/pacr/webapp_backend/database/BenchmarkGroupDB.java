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
        List<BenchmarkGroup> groups = new LinkedList<>();
        this.findAll().forEach(groups::add);
        return groups;
    }

    @Override
    default BenchmarkGroup getBenchmarkGroup(int id) {
        return this.findById(id).orElse(null);
    }

    @Override
    default int saveBenchmarkGroup(BenchmarkGroup group) {
        this.save(group);
        return group.getId();
    }

    @Override
    default void deleteGroup(BenchmarkGroup group) {
        this.delete(group);
    }
}
