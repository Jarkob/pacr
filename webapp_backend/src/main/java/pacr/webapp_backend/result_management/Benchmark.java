package pacr.webapp_backend.result_management;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * The name this benchmark object was created with. Cannot be changed after creating the benchmark.
     */
    private String originalName;

    /**
     * The custom name of this benchmark object that can be changed.
     */
    private String customName;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<BenchmarkProperty> properties;

    @ManyToOne
    private BenchmarkGroup group;

    /**
     * Creates an empty benchmark. Necessary for jpa database entities.
     */
    public Benchmark() {
    }

    /**
     * Creates a benchmark with a name. This name is used both as the original name and the custom name.
     * The description and list of properties are empty, and this Benchmark is not associated with a BenchmarkGroup.
     * @param originalName the original name of the benchmark. Throws IllegalArgumentException if it is null, empty or
     *                     blank.
     */
    public Benchmark(@NotNull String originalName) {
        if (originalName == null || originalName.isEmpty() || originalName.isBlank()) {
            throw new IllegalArgumentException("originalName cannot be null, empty or blank");
        }
        this.originalName = originalName;
        this.customName = originalName;
        this.description = "";
        this.properties = new HashSet<>();
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

    /**
     * Gets the list of all properties that are part of this benchmark.
     * @return the list of properties.
     */
    public Set<BenchmarkProperty> getProperties() {
        return properties;
    }

    /**
     * Gets the BenchmarkGroup of this benchmark.
     * @return the group.
     */
    public BenchmarkGroup getGroup() {
        return group;
    }

    /**
     * Sets the custom name to a new name as long as its not null, empty or blank. Otherwise the custom name remains the
     * same.
     * @param customName the new custom name.
     */
    public void setCustomName(String customName) {
        if (customName != null && !customName.isEmpty() && !customName.isBlank()) {
            this.customName = customName;
        }
    }

    /**
     * Sets the description to a new description as long as its not null.
     * @param description the new description.
     */
    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    /**
     * Sets the group that this benchmark belongs to to a new group. The new group may be null if this benchmark
     * belongs to no group.
     * @param group the new group.
     */
    public void setGroup(BenchmarkGroup group) {
        this.group = group;
    }

    /**
     * Adds a new property to the list of properties of this benchmark.
     * If the list already contains this property or the property is null, no action is taken.
     * @param property the new property.
     */
    public void addProperty(BenchmarkProperty property) {
        if (property != null) {
            this.properties.add(property);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Benchmark benchmark = (Benchmark) obj;
        return id == benchmark.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }
}
