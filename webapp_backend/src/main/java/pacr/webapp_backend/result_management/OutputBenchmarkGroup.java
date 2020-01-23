package pacr.webapp_backend.result_management;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a benchmark group for output purposes.
 */
public class OutputBenchmarkGroup {
    private OutputBenchmark[] benchmarks;

    private int id;
    private String name;

    /**
     * Creates a new OutputBenchmarkGroup with a number of benchmarks and metadata of the group.
     * @param benchmarks the benchmarks. Cannot be null.
     * @param group the BenchmarkGroup that metadata is copied from. Cannot be null.
     */
    public OutputBenchmarkGroup(@NotNull OutputBenchmark[] benchmarks, @NotNull BenchmarkGroup group) {
        Objects.requireNonNull(benchmarks);
        Objects.requireNonNull(group);

        this.benchmarks = benchmarks;
        this.id = group.getId();
        this.name = group.getName();
    }

    /**
     * Creates a new OutputBenchmarkGroup with a number of benchmarks that belong to no group. The name of this
     * OutputBenchmarkGroup will be empty to represent this.
     * @param benchmarks the benchmarks. Cannot be null.
     */
    public OutputBenchmarkGroup(@NotNull OutputBenchmark[] benchmarks) {
        Objects.requireNonNull(benchmarks);

        this.benchmarks = benchmarks;
        this.id = -1;
        this.name = "";
    }

    /**
     * Gets the id of the group.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the group.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all benchmarks of this group.
     * @return the benchmarks.
     */
    public List<OutputBenchmark> getBenchmarks() {
        return Arrays.asList(benchmarks);
    }
}
