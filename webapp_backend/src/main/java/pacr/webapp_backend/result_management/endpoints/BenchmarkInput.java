package pacr.webapp_backend.result_management.endpoints;

import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Metadata of a benchmark that was edited in the front end.
 */
public class BenchmarkInput {
    private int id;
    private String customName;
    private String description;
    private int groupId;

    /**
     * Empty constructor for spring.
     */
    public BenchmarkInput() {
    }

    /**
     * Creates a new BenchmarkInput.
     * @param id the id of the benchmark.
     * @param originalName the original name.
     * @param customName the custom name.
     * @param description the description.
     * @param groupId the id of the group of the benchmark. -1 indicates this benchmark has no group.
     */
    public BenchmarkInput(int id, @NotNull String originalName, @NotNull String customName, @NotNull String description,
                          int groupId) {
        this.id = id;
        this.originalName = originalName;
        this.customName = customName;
        this.description = description;
        this.groupId = groupId;

        if (!validate()) {
            throw new IllegalArgumentException("input cannot be null, empty or blank");
        }
    }

    /**
     * @return the id of the benchmark.
     */
    public int getId() {
        return id;
    }

    /**
     * @return the original name of the benchmark.
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @return the custom name of the benchmark.
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * @return the description of the benchmark.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the id of the group of the benchmark. -1 indicates this benchmark has no group.
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * @return {@code true} if the attributes of this benchmark are valid, otherwise {@code false}.
     */
    public boolean validate() {
        return StringUtils.hasText(customName) && description != null;
    }
}
