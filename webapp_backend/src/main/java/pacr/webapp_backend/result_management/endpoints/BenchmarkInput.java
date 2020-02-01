package pacr.webapp_backend.result_management.endpoints;

import org.springframework.util.StringUtils;

/**
 * Metadata of a benchmark that was edited in the front end.
 */
public class BenchmarkInput {
    private int id;
    private String customName;
    private String description;
    private int groupId;

    public BenchmarkInput() {
    }

    public int getId() {
        return id;
    }

    public String getCustomName() {
        return customName;
    }

    public String getDescription() {
        return description;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean validate() {
        return StringUtils.hasText(customName) && description != null;
    }
}
