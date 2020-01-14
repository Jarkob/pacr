package pacr.webapp_backend.result_management;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a benchmark that measures certain properties.
 * This entity is saved in the database.
 */
@Entity(name = "Benchmark")
@Table(name = "benchmark")
public class Benchmark {

    @Id
    @GeneratedValue
    private int id;

    private String benchmarkName;
    private String customName;
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BenchmarkProperty> propertyList;

    @ManyToOne(cascade = CascadeType.ALL)
    private BenchmarkGroup group;

    /**
     * Creates an empty benchmark. Necessary for jpa database entities.
     */
    Benchmark() {
    }

    /**
     * Creates a benchmark with a name. This name is used both as the original name and the custom name.
     * The description and list of properties are empty, and this Benchmark is not associated with a BenchmarkGroup.
     * @param benchmarkName the original name of the benchmark.
     */
    public Benchmark(String benchmarkName) {
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
    public int getId() {
        return id;
    }

    /**
     * Gets the original name of the benchmark with which it was created.
     * @return the original name.
     */
    public String getBenchmarkName() {
        return benchmarkName;
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

    /**
     * Gets the list of all properties that are part of this benchmark.
     * @return the list of properties.
     */
    public List<BenchmarkProperty> getPropertyList() {
        return propertyList;
    }

    /**
     * Gets the BenchmarkGroup of this benchmark.
     * @return the group.
     */
    public BenchmarkGroup getGroup() {
        return group;
    }

    /**
     * Sets the custom name to a new name.
     * @param customName the new custom name.
     */
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    /**
     * Sets the description to a new description.
     * @param description the new description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the group that this benchmark belongs to to a new group.
     * @param group the new group.
     */
    public void setGroup(BenchmarkGroup group) {
        this.group = group;
    }

    /**
     * Adds a new property to the list of properties of this benchmark.
     * If the list already contains this property, no action is taken.
     * @param property the new property.
     */
    public void addProperty(BenchmarkProperty property) {
        if (!this.propertyList.contains(property)) {
            this.propertyList.add(property);
        }
    }
}
