package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a benchmark for output purposes.
 */
public class OutputBenchmark implements IBenchmark {

    private OutputPropertyResult[] results;

    private int id;
    private String originalName;
    private String customName;
    private String description;

    /**
     * Creates a new OutputBenchmark with properties (including results) and metadata of the benchmark.
     * @param results the properties with results. Cannot be null.
     * @param benchmark the benchmark that metadata is copied from. Cannot be null.
     */
    public OutputBenchmark(@NotNull OutputPropertyResult[] results, @NotNull Benchmark benchmark) {
        Objects.requireNonNull(results);
        Objects.requireNonNull(benchmark);

        this.results = results;

        this.id = benchmark.getId();
        this.originalName = benchmark.getOriginalName();
        this.customName = benchmark.getCustomName();
        this.description = benchmark.getDescription();
    }

    @Override
    public Map<String, IBenchmarkProperty> getBenchmarkProperties() {
        Map<String, IBenchmarkProperty> properties = new HashMap<>();

        for (OutputPropertyResult result : results) {
            properties.put(result.getName(), result);
        }

        return properties;
    }

    /**
     * Gets a list of all properties for output of this benchmark.
     * @return the properties.
     */
    public List<OutputPropertyResult> getPropertiesList() {
        return Arrays.asList(results);
    }

    /**
     * Gets the id of the benchmark.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the original name of the benchmark.
     * @return the original name.
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * Gets the custom name of the benchmark.
     * @return the custom name.
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * Gets the description of the benchmark.
     * @return the description.
     */
    public String getDescription() {
        return description;
    }
}
