package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a benchmark for output purposes.
 */
public class OutputBenchmark implements IBenchmark {

    private IOutputPropertyResult[] results;
    private Benchmark benchmark;

    /**
     * Creates a new OutputBenchmark with properties (including results) that is backed by a Benchmark.
     * @param results the properties with results.
     * @param benchmark the benchmark that backs this output entity.
     */
    OutputBenchmark(IOutputPropertyResult[] results, Benchmark benchmark) {
        this.results = results;
        this.benchmark = benchmark;
    }

    @Override
    public Map<String, IBenchmarkProperty> getBenchmarkProperties() {
        Map<String, IBenchmarkProperty> properties = new HashMap<>();
        for (IOutputPropertyResult result : results) {
            properties.put(result.getName(), result);
        }
        return properties;
    }

    /**
     * Gets a list of all properties for output of this benchmark.
     * @return the properties.
     */
    public List<IOutputPropertyResult> getPropertiesList() {
        return Arrays.asList(results);
    }

    /**
     * Gets the id of the benchmark.
     * @return the id.
     */
    public int getId() {
        return benchmark.getId();
    }

    /**
     * Gets the original name of the benchmark.
     * @return the original name.
     */
    public String getOriginalName() {
        return benchmark.getBenchmarkName();
    }

    /**
     * Gets the custom name of the benchmark.
     * @return the custom name.
     */
    public String getCustomName() {
        return benchmark.getCustomName();
    }

    /**
     * Gets the description of the benchmark.
     * @return the description.
     */
    public String getDescription() {
        return benchmark.getDescription();
    }

    /**
     * Gets the group of the benchmark.
     * @return the group.
     */
    public BenchmarkGroup getBenchmarkGroup() {
        return benchmark.getGroup();
    }
}
