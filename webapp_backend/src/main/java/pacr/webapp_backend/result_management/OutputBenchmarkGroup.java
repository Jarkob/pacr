package pacr.webapp_backend.result_management;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a benchmark group for output purposes.
 */
public class OutputBenchmarkGroup {
    private OutputBenchmark[] benchmarks;
    private BenchmarkGroup group;

    /**
     * Creates a new OutputBenchmarkGroup with a number of benchmarks that is backed by a BenchmarkGroup.
     * @param benchmarks the benchmarks.
     * @param group the BenchmarkGroup that backs this output entity.
     */
    OutputBenchmarkGroup(OutputBenchmark[] benchmarks, BenchmarkGroup group) {
        this.benchmarks = benchmarks;
        this.group = group;
    }

    /**
     * Gets the id of the group.
     * @return the id.
     */
    public int getId() {
        return group.getId();
    }

    /**
     * Gets the name of the group.
     * @return the name.
     */
    public String getName() {
        return group.getName();
    }

    /**
     * Gets all benchmarks of this group.
     * @return the benchmarks.
     */
    public List<OutputBenchmark> getBenchmarks() {
        return Arrays.asList(benchmarks);
    }
}
