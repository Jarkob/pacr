package pacr.webapp_backend.result_management.endpoints;

import lombok.Getter;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Metadata of a benchmark that was edited in the front end.
 */
@Getter
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
     * @param customName the custom name.
     * @param description the description.
     * @param groupId the id of the group of the benchmark. -1 indicates this benchmark has no group.
     */
    public BenchmarkInput(int id, @NotNull String customName, @NotNull String description,
                          int groupId) {
        this.id = id;
        this.customName = customName;
        this.description = description;
        this.groupId = groupId;

        if (!validate()) {
            throw new IllegalArgumentException("input cannot be null, empty or blank");
        }
    }

    /**
     * @return {@code true} if the attributes of this benchmark are valid, otherwise {@code false}.
     */
    public boolean validate() {
        return StringUtils.hasText(customName) && description != null;
    }
}
