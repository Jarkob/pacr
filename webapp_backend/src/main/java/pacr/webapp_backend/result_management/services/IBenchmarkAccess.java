package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.benchmarker_communication.services.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;

import java.util.Collection;

/**
 * Saves benchmark related objects in the database and retrieves them.
 */
public interface IBenchmarkAccess {
    /**
     * Gets all saved benchmarks.
     * @return all benchmarks.
     */
    Collection<Benchmark> getAllBenchmarks();

    /**
     * Gets all saved benchmark groups.
     * @return all groups.
     */
    Collection<BenchmarkGroup> getAllGroups();

    /**
     * Gets the benchmark with the entered id. If no such benchmark is saved, returns null.
     * @param id the benchmark id.
     * @return the benchmark.
     */
    Benchmark getBenchmark(int id);

    /**
     * Gets the benchmark group with the entered id. If no such group is saved, returns null.
     * @param id the group id.
     * @return the group.
     */
    BenchmarkGroup getBenchmarkGroup(int id);

    /**
     * Saves the given benchmark or updates it in the database.
     * @param benchmark the benchmark.
     */
    void saveBenchmark(Benchmark benchmark);

    /**
     * Saves the given group or updates it in the database.
     * @param group the group
     * @return the groups id.
     */
    int saveBenchmarkGroup(BenchmarkGroup group);

    /**
     * Deletes the given group in the database.
     * @param group the group.
     */
    void deleteGroup(BenchmarkGroup group);
}
