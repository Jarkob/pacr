package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.Benchmark;
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
        List<Benchmark> benchmarks = new LinkedList<>();
        this.findAll().forEach(benchmarks::add);
        return benchmarks;
    }

    @Override
    default Benchmark getBenchmark(int id) {
        return this.findById(id).orElse(null);
    }


    @Override
    default void saveBenchmark(Benchmark benchmark) {
        this.save(benchmark);
    }
}
