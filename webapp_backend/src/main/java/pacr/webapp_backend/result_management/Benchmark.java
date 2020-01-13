package pacr.webapp_backend.result_management;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a benchmark that measures certain properties.
 * This entity is saved in the database.
 */
class Benchmark {
    private int id;
    private String benchmarkName;
    private String customName;
    private String description;
    private List<BenchmarkProperty> propertyList;
    private BenchmarkGroup group;

    /**
     * Creates a benchmark with a name. This name is used both as the original name and the custom name.
     * The description and list of properties are empty, and this Benchmark is not associated with a BenchmarkGroup.
     * @param benchmarkName the original name of the benchmark.
     */
    Benchmark(String benchmarkName) {
        this.benchmarkName = benchmarkName;
        this.customName = benchmarkName;
        this.description = "";
        this.propertyList = new LinkedList<>();
        this.group = null;
    }

    /**
     * Gets the unique id of the benchmark.
     * @return the id.
     */
    int getId() {
        return id;
    }

    /**
     * Gets the original name of the benchmark with which it was created.
     * @return the original name.
     */
    String getBenchmarkName() {
        return benchmarkName;
    }

    /**
     * Gets the custom name of the benchmark.
     * @return the custom name.
     */
    String getCustomName() {
        return customName;
    }

    /**
     * Gets the description of the benchmark.
     * @return the description.
     */
    String getDescription() {
        return description;
    }

    /**
     * Gets the list of all properties that are part of this benchmark.
     * @return the list of properties.
     */
    List<BenchmarkProperty> getPropertyList() {
        return propertyList;
    }

    /**
     * Gets the BenchmarkGroup of this benchmark.
     * @return the group.
     */
    BenchmarkGroup getGroup() {
        return group;
    }

    /**
     * Sets the custom name to a new name.
     * @param customName the new custom name.
     */
    void setCustomName(String customName) {
        this.customName = customName;
    }

    /**
     * Sets the description to a new description.
     * @param description the new description.
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the group that this benchmark belongs to to a new group.
     * @param group the new group.
     */
    void setGroup(BenchmarkGroup group) {
        this.group = group;
    }

    /**
     * Adds a new property to the list of properties of this benchmark.
     * If the list already contains this property, no action is taken.
     * @param property the new property.
     */
    void addProperty(BenchmarkProperty property) {
        if (!this.propertyList.contains(property)) {
            this.propertyList.add(property);
        }
    }
}
