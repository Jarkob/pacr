package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;

import java.util.Collection;

/**
 * Saves benchmark objects in the database and retrieves them.
 */
public interface IBenchmarkAccess {
    /**
     * Gets all saved benchmarks.
     * @return all benchmarks.
     */
    Collection<Benchmark> getAllBenchmarks();

    /**
     * Gets the benchmark with the entered id. If no such benchmark is saved, returns null.
     * @param id the benchmark id.
     * @return the benchmark.
     */
    Benchmark getBenchmark(int id);

    /**
     * Saves the given benchmark or updates it in the database.
     * @param benchmark the benchmark.
     */
    void saveBenchmark(Benchmark benchmark);

    /**
     * Gets all the benchmarks of a given group.
     * @param group the group.
     * @return the benchmarks.
     */
    Collection<Benchmark> getBenchmarksOfGroup(@Nullable BenchmarkGroup group);
}
