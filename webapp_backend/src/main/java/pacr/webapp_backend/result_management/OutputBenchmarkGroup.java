package pacr.webapp_backend.result_management;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a benchmark group for output purposes.
 */
public class OutputBenchmarkGroup {
    private OutputBenchmark[] benchmarks;

    private int id;
    private String name;

    /**
     * Creates a new OutputBenchmarkGroup with a number of benchmarks and metadata of the group.
     * Throws IllegalArgumentException if a parameter is null.
     * @param benchmarks the benchmarks.
     * @param group the BenchmarkGroup that metadata is copied from.
     */
    OutputBenchmarkGroup(@NotNull OutputBenchmark[] benchmarks, @NotNull BenchmarkGroup group) {
        if (benchmarks == null || group == null) {
            throw new IllegalArgumentException("benchmarks or group cannot be null");
        }
        this.benchmarks = benchmarks;
        this.id = group.getId();
        this.name = group.getName();
    }

    /**
     * Creates a new OutputBenchmarkGroup with a number of benchmarks that belong to no group. The name of this
     * OutputBenchmarkGroup will be empty to represent this.
     * @param benchmarks the benchmarks. Throws IllegalArgumentException if this is null.
     */
    OutputBenchmarkGroup(@NotNull OutputBenchmark[] benchmarks) {
        if (benchmarks == null) {
            throw new IllegalArgumentException("benchmarks cannot be null");
        }
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
