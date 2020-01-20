package pacr.webapp_backend.result_management.services;

import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;
import pacr.webapp_backend.result_management.BenchmarkProperty;

import javax.validation.constraints.NotNull;
import java.util.Collection;

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
    Collection<Benchmark> getAllBenchmarks() {
        return benchmarkAccess.getAllBenchmarks();
    }

    /**
     * @return all saved benchmark groups.
     */
    Collection<BenchmarkGroup> getAllGroups() {
        return groupAccess.getAllGroups();
    }

    /**
     * Updates the given benchmark in the database (or creates it if it hasn't been saved yet).
     * Also updates associated benchmark properties but not associated groups.
     * @param benchmark the benchmark. Throws IllegalArgumentException if this is null.
     */
    void createOrUpdateBenchmark(@NotNull Benchmark benchmark) {
        if (benchmark == null) {
            throw new IllegalArgumentException("benchmark cannot be null");
        }
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

    /**
     * Updates the benchmark with the entered id with the given meta data (and adds the benchmark to the group). The new
     * name overwrites the custom name, not the original name of the benchmark.
     * @param benchmarkID the id of the benchmark.
     * @param name the new custom name. Throws IllegalArgumentException if it is null, empty or blank.
     * @param description The new description. Throws IllegalArgumentException if it is null.
     * @param groupID the id of the new group. -1 causes this benchmark not to be associated with any group.
     * @throws NotFoundException if no benchmark and/or group with the given id(s) is saved in the database.
     */
    void updateBenchmark(int benchmarkID, @NotNull String name, @NotNull String description, int groupID)
            throws NotFoundException {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }
        if (description == null) {
            throw new IllegalArgumentException("description cannot be null");
        }

        Benchmark benchmark = benchmarkAccess.getBenchmark(benchmarkID);
        if (benchmark == null) {
            throw new NotFoundException("no benchmark with id " + benchmarkID);
        }

        BenchmarkGroup group = null;

        // get new group from database unless groupID is set to GROUP_ID_NO_GROUP (in that case the group of the
        // benchmark will be set to null)
        if (groupID != GROUP_ID_NO_GROUP) {
            group = this.groupAccess.getBenchmarkGroup(groupID);
            if (group == null) {
                throw new NotFoundException("no group with id " + groupID);
            }
        }
        
        // add benchmark to new group (or sets group to null if groupID was set to GROUP_ID_NO_GROUP)
        benchmark.setGroup(group);

        benchmark.setCustomName(name);
        benchmark.setDescription(description);

        this.benchmarkAccess.saveBenchmark(benchmark);
    }

    /**
     * Creates and saves a new benchmark group. This new group is associated with no benchmarks.
     * @param name the name of the new benchmark group. Throws IllegalArgumentException if this is null, empty or blank.
     */
    void addGroup(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }
        BenchmarkGroup group = new BenchmarkGroup(name);
        this.groupAccess.saveBenchmarkGroup(group);
    }

    /**
     * The name of the group is updated to the given name.
     * @param id the id of the group.
     * @param name the new name of the group. Throws IllegalArgumentException if this is null, empty or blank.
     * @throws NotFoundException if no group with this id is saved in the database.
     */
    void updateGroup(int id, String name) throws NotFoundException {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }

        BenchmarkGroup group = this.groupAccess.getBenchmarkGroup(id);

        if (group == null) {
            throw new NotFoundException("no group with id " + id);
        }

        group.setName(name);

        this.groupAccess.saveBenchmarkGroup(group);
    }

    /**
     * Deletes the group. Benchmarks that are still associated with this group now belong to no group.
     * @param id the id of the group.
     * @throws NotFoundException if no group with this id is saved in the database.
     */
    void deleteGroup(int id) throws NotFoundException {
        BenchmarkGroup group = this.groupAccess.getBenchmarkGroup(id);

        if (group == null) {
            throw new NotFoundException("no group with id " + id);
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
