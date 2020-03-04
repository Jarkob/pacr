package pacr.webapp_backend.result_management.services;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Manages benchmark meta data and saves new benchmarks.
 */
@Component
public class BenchmarkManager {

    private static final Logger LOGGER = LogManager.getLogger(BenchmarkManager.class);

    /**
     * The BenchmarkGroup object that represents no group.
     */
    private static BenchmarkGroup standardGroup;

    private IBenchmarkAccess benchmarkAccess;
    private IBenchmarkGroupAccess groupAccess;

    /**
     * Creates a new BenchmarkManager through constructor injection.
     * @param benchmarkAccess the injected benchmark access object.
     * @param groupAccess the injected benchmark group access object.
     */
    public BenchmarkManager(final IBenchmarkAccess benchmarkAccess, final IBenchmarkGroupAccess groupAccess) {
        this.benchmarkAccess = benchmarkAccess;
        this.groupAccess = groupAccess;

        standardGroup = groupAccess.getStandardGroup();
        if (standardGroup == null) {
            LOGGER.info("creating and saving new standard group");

            standardGroup = new BenchmarkGroup(BenchmarkGroup.STANDARD_GROUP_NAME);
            standardGroup.setToStandardGroup();
            groupAccess.saveBenchmarkGroup(standardGroup);
        }
    }

    /**
     * @return the id of the standard group.
     */
    static int getStandardGroupId() {
        return standardGroup.getId();
    }

    /**
     * @return all saved benchmarks.
     */
    public Collection<Benchmark> getAllBenchmarks() {
        return benchmarkAccess.getAllBenchmarks();
    }

    /**
     * Gets all benchmarks of a group.
     * @param groupId the id of the group.
     * @return the benchmarks of the group.
     */
    public Collection<Benchmark> getBenchmarksByGroup(final int groupId) {
        final BenchmarkGroup group = groupAccess.getBenchmarkGroup(groupId);

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
     * If the benchmark has no group (null), it is added to the standard group.
     * Enters Benchmark.class monitor.
     * @param benchmark the benchmark. Throws IllegalArgumentException if this is null.
     */
    void createOrUpdateBenchmark(@NotNull final Benchmark benchmark) {
        Objects.requireNonNull(benchmark);

        if (benchmark.getGroup() == null) {
            benchmark.setGroup(standardGroup);
        }

        synchronized (Benchmark.class) {
            this.benchmarkAccess.saveBenchmark(benchmark);

            // saveBenchmark creates a merge between the java object and the database representation. The problem is:
            // persist is not called directly on the children, but rather copies of them. so the id is never set in the
            // original java object. This is a workaround to fix this by setting the ids manually after the fact.
            final Benchmark savedBenchmark = benchmarkAccess.getBenchmark(benchmark.getId());
            for (final BenchmarkProperty savedProperty : savedBenchmark.getProperties()) {
                for (final BenchmarkProperty property : benchmark.getProperties()) {
                    if (property.equals(savedProperty)) {
                        property.setId(savedProperty.getId());
                        break;
                    }
                }
            }
        }

        LOGGER.info("created or updated benchmark {}", benchmark.getOriginalName());
    }

    /**
     * Updates the benchmark with the entered id with the given meta data (and adds the benchmark to the group). The new
     * name overwrites the custom name, not the original name of the benchmark.
     * Enters Benchmark.class monitor.
     * @param benchmarkID the id of the benchmark.
     * @param name the new custom name. Throws IllegalArgumentException if it is null, empty or blank.
     * @param description The new description. Throws IllegalArgumentException if it is null.
     * @param groupID the id of the new group.
     */
    public void updateBenchmark(final int benchmarkID, @NotNull final String name, @NotNull final String description, final int groupID) {
        Objects.requireNonNull(description);
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }

        synchronized (Benchmark.class) {
            final Benchmark benchmark = benchmarkAccess.getBenchmark(benchmarkID);
            if (benchmark == null) {
                LOGGER.error("no benchmark with id {} - unable to update", benchmarkID);
                return;
            }

            final BenchmarkGroup group = this.groupAccess.getBenchmarkGroup(groupID);
            if (group == null) {
                LOGGER.error("no benchmark group with id {} - unable to update benchmark {}", groupID, benchmarkID);
                return;
            }

            benchmark.setGroup(group);
            benchmark.setCustomName(name);
            benchmark.setDescription(description);

            this.benchmarkAccess.saveBenchmark(benchmark);
        }

        LOGGER.info("updated benchmark with id {}", benchmarkID);
    }

    /**
     * Creates and saves a new benchmark group. This new group is associated with no benchmarks.
     * Enters Benchmark.class monitor.
     * @param name the name of the new benchmark group. Throws IllegalArgumentException if this is null, empty or blank.
     */
    public void addGroup(@NotNull final String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }

        final BenchmarkGroup group = new BenchmarkGroup(name);

        synchronized (Benchmark.class) {
            this.groupAccess.saveBenchmarkGroup(group);
        }

        LOGGER.info("created benchmark group {} with id {}", name, group.getId());
    }

    /**
     * The name of the group is updated to the given name.
     * Enters Benchmark.class monitor.
     * @param id the id of the group.
     * @param name the new name of the group. Throws IllegalArgumentException if this is null, empty or blank.
     */
    public void updateGroup(final int id, @NotNull final String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }

        synchronized (Benchmark.class) {
            final BenchmarkGroup group = this.groupAccess.getBenchmarkGroup(id);

            if (group == null) {
                LOGGER.error("no benchmark group with id {} - unable to update", id);
                return;
            }

            group.setName(name);

            this.groupAccess.saveBenchmarkGroup(group);
        }

        LOGGER.info("updated benchmark group with id {}", id);
    }

    /**
     * Deletes the group. Benchmarks that are still associated with this group now belong to no group.
     * Enters Benchmark.class monitor.
     * @param id the id of the group.
     * @throws IllegalAccessException if it is attempted to delete the standard group.
     */
    public void deleteGroup(final int id) throws IllegalAccessException {
        if (standardGroup.getId() == id) {
            throw new IllegalAccessException("the standard group cannot be deleted");
        }

        synchronized (Benchmark.class) {
            final BenchmarkGroup group = this.groupAccess.getBenchmarkGroup(id);

            if (group == null) {
                throw new NoSuchElementException("no group with id " + id);
            }

            for (final Benchmark benchmark : this.getAllBenchmarks()) {
                if (group.equals(benchmark.getGroup())) {
                    benchmark.setGroup(standardGroup);
                    this.benchmarkAccess.saveBenchmark(benchmark);

                    LOGGER.info("group of benchmark {} set to standard (previous group {} is getting deleted)",
                            benchmark.getId(), id);
                }
            }

            this.groupAccess.deleteGroup(group);
        }

        LOGGER.info("deleted group {}", id);
    }
}
