package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.services.Benchmark;
import pacr.webapp_backend.result_management.services.BenchmarkGroup;
import pacr.webapp_backend.result_management.services.IBenchmarkAccess;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a default implementation of the IBenchmarkAccess interface.
 */
@Component
public interface BenchmarkDB extends CrudRepository<Benchmark, Integer>, IBenchmarkAccess {

    @Override
    default Collection<Benchmark> getAllBenchmarks() {
        final List<Benchmark> benchmarks = new LinkedList<>();
        this.findAll().forEach(benchmarks::add);
        return benchmarks;
    }

    @Override
    default Benchmark getBenchmark(final int id) {
        return this.findById(id).orElse(null);
    }

    @Override
    default void saveBenchmark(final Benchmark benchmark) {
        this.save(benchmark);
    }

    @Override
    default Collection<Benchmark> getBenchmarksOfGroup(@Nullable final BenchmarkGroup group) {
        return findBenchmarksByGroup(group);
    }

    /**
     * Custom jpa method. Gets all benchmarks associated with the given group.
     * @param group the group.
     * @return the benchmarks.
     */
    Collection<Benchmark> findBenchmarksByGroup(BenchmarkGroup group);
}
