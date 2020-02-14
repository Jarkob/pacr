package pacr.webapp_backend.result_management.services;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Represents a benchmark for output purposes.
 * '@Getter' provides this class with all getters so the json can be properly created.
 */
@Getter
public class OutputBenchmark {

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

    /**
     * @return Gets the id of the group the benchmark belongs to. -1 if the benchmark belongs to no group.
     */
    public int getGroupId() {
        return groupId;
    }
}
