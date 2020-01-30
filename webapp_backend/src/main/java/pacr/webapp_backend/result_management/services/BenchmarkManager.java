package pacr.webapp_backend.result_management.services;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;
import pacr.webapp_backend.result_management.BenchmarkProperty;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Manages benchmark meta data and saves new benchmarks.
 */
@Component
public class BenchmarkManager {
    /**
     * The id that needs to be passed in order to communicate that a benchmark has no group.
     */
    public static final int GROUP_ID_NO_GROUP = -1;

    private IBenchmarkAccess benchmarkAccess;
    private IBenchmarkGroupAccess groupAccess;

    /**
     * Creates a new BenchmarkManager through constructor injection.
     * @param benchmarkAccess the injected benchmark access object.
     * @param groupAccess the injected benchmark group access object.
     */
    public BenchmarkManager(IBenchmarkAccess benchmarkAccess, IBenchmarkGroupAccess groupAccess) {
        this.benchmarkAccess = benchmarkAccess;
        this.groupAccess = groupAccess;
    }

    /**
     * @return all saved benchmarks.
     */
    public Collection<Benchmark> getAllBenchmarks() {
        return benchmarkAccess.getAllBenchmarks();
    }

    /**
     * Gets all benchmarks of a group. Gets all benchmarks with no group if the given id is -1.
     * @param groupId the id of the group.
     * @return the benchmarks of the group or of no group.
     */
    public Collection<Benchmark> getBenchmarksByGroup(int groupId) {
        BenchmarkGroup group = null;
        if (groupId != GROUP_ID_NO_GROUP) {
            group = groupAccess.getBenchmarkGroup(groupId);
        }

        return benchmarkAccess.getBenchmarksOfGroup(group);
    }

    /**
     * @return all saved benchmark groups.
     */
    public Collection<BenchmarkGroup> getAllGroups() {
        return groupAccess.getAllGroups();
    }

    /**
     * Updates the given benchmark in the database (or creates it if it hasn't been saved yet).
     * Also updates associated benchmark properties but not associated groups.
     * Enters Benchmark.class monitor
     * @param benchmark the benchmark. Throws IllegalArgumentException if this is null.
     */
    void createOrUpdateBenchmark(@NotNull Benchmark benchmark) {
        if (benchmark == null) {
            throw new IllegalArgumentException("benchmark cannot be null");
        }

        synchronized (Benchmark.class) {
            this.benchmarkAccess.saveBenchmark(benchmark);

            // TODO figure out if the bug that caused this workaround can be fixed. Because I have given up for now.
            // for some reason jpa sometimes won't set the ids of the properties (even though they have been created in the
            // database). This is a workaround to fix this.
            Benchmark savedBenchmark = benchmarkAccess.getBenchmark(benchmark.getId());
            for (BenchmarkProperty savedProperty : savedBenchmark.getProperties()) {
                for (BenchmarkProperty property : benchmark.getProperties()) {
                    if (property.getName().equals(savedProperty.getName())) {
                        property.setId(savedProperty.getId());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Updates the benchmark with the entered id with the given meta data (and adds the benchmark to the group). The new
     * name overwrites the custom name, not the original name of the benchmark.
     * Enters Benchmark.class monitor.
     * @param benchmarkID the id of the benchmark.
     * @param name the new custom name. Throws IllegalArgumentException if it is null, empty or blank.
     * @param description The new description. Throws IllegalArgumentException if it is null.
     * @param groupID the id of the new group. -1 causes this benchmark not to be associated with any group.
     */
    public void updateBenchmark(int benchmarkID, @NotNull String name, @NotNull String description, int groupID) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }
        if (description == null) {
            throw new IllegalArgumentException("description cannot be null");
        }

        synchronized (Benchmark.class) {
            Benchmark benchmark = benchmarkAccess.getBenchmark(benchmarkID);
            if (benchmark == null) {
                throw new NoSuchElementException("no benchmark with id " + benchmarkID);
            }

            BenchmarkGroup group = null;

            // get new group from database unless groupID is set to GROUP_ID_NO_GROUP (in that case the group of the
            // benchmark will be set to null)
            if (groupID != GROUP_ID_NO_GROUP) {
                group = this.groupAccess.getBenchmarkGroup(groupID);
                if (group == null) {
                    throw new NoSuchElementException("no group with id " + groupID);
                }
            }

            // add benchmark to new group (or sets group to null if groupID was set to GROUP_ID_NO_GROUP)
            benchmark.setGroup(group);

            benchmark.setCustomName(name);
            benchmark.setDescription(description);

            this.benchmarkAccess.saveBenchmark(benchmark);
        }
    }

    /**
     * Creates and saves a new benchmark group. This new group is associated with no benchmarks.
     * Enters Benchmark.class monitor.
     * @param name the name of the new benchmark group. Throws IllegalArgumentException if this is null, empty or blank.
     */
    public void addGroup(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }

        BenchmarkGroup group = new BenchmarkGroup(name);

        synchronized (Benchmark.class) {
            this.groupAccess.saveBenchmarkGroup(group);
        }
    }

    /**
     * The name of the group is updated to the given name.
     * Enters Benchmark.class monitor.
     * @param id the id of the group.
     * @param name the new name of the group. Throws IllegalArgumentException if this is null, empty or blank.
     */
    public void updateGroup(int id, String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }

        synchronized (Benchmark.class) {
            BenchmarkGroup group = this.groupAccess.getBenchmarkGroup(id);

            if (group == null) {
                throw new NoSuchElementException("no group with id " + id);
            }

            group.setName(name);

            this.groupAccess.saveBenchmarkGroup(group);
        }
    }

    /**
     * Deletes the group. Benchmarks that are still associated with this group now belong to no group.
     * Enters Benchmark.class monitor.
     * @param id the id of the group.
     */
    public void deleteGroup(int id) {
        synchronized (Benchmark.class) {
            BenchmarkGroup group = this.groupAccess.getBenchmarkGroup(id);

            if (group == null) {
                throw new NoSuchElementException("no group with id " + id);
            }

            for (Benchmark benchmark : this.getAllBenchmarks()) {
                if (group.equals(benchmark.getGroup())) {
                    benchmark.setGroup(null);
                    this.benchmarkAccess.saveBenchmark(benchmark);
                }
            }

            this.groupAccess.deleteGroup(group);
        }
    }
}
