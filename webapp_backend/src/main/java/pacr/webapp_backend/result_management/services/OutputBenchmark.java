package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a benchmark for output purposes.
 */
public class OutputBenchmark implements IBenchmark {

    private static final transient int NO_GROUP_ID = -1;

    private int id;
    private String originalName;
    private String customName;
    private String description;
    private int groupId;

    private OutputPropertyResult[] results;

    /**
     * Creates a new OutputBenchmark with properties (including results) and metadata of the benchmark.
     * @param results the properties with results. Cannot be null.
     * @param benchmark the benchmark that metadata is copied from. Cannot be null.
     */
    OutputBenchmark(@NotNull OutputPropertyResult[] results, @NotNull Benchmark benchmark) {
        Objects.requireNonNull(results);
        Objects.requireNonNull(benchmark);

        this.results = results;

        this.id = benchmark.getId();
        this.originalName = benchmark.getOriginalName();
        this.customName = benchmark.getCustomName();
        this.description = benchmark.getDescription();

        BenchmarkGroup group = benchmark.getGroup();

        if (group != null) {
            this.groupId = group.getId();
        } else {
            this.groupId = NO_GROUP_ID;
        }
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
     * Gets the original name of the benchmark.
     * @return the original name.
     */
    String getOriginalName() {
        return originalName;
    }

    /**
     * @return Gets the id of the group the benchmark belongs to. -1 if the benchmark belongs to no group.
     */
    int getGroupId() {
        return groupId;
    }
}
