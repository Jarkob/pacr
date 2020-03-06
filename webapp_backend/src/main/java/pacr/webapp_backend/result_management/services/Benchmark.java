package pacr.webapp_backend.result_management.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.util.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a benchmark that measures certain properties.
 * This entity is saved in the database.
 */
@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Benchmark {

    @Id
    @GeneratedValue
    private int id;

    /**
     * The name this benchmark object was created with. Cannot be changed after creating the benchmark.
     */
    @EqualsAndHashCode.Include
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
    public Benchmark(@NotNull final String originalName) {
        if (!StringUtils.hasText(originalName)) {
            throw new IllegalArgumentException("originalName cannot be null, empty or blank");
        }
        this.originalName = originalName;
        this.customName = originalName;
        this.description = "";
        this.properties = new HashSet<>();
        this.group = null;
    }

    /**
     * Sets the custom name to a new name as long as its not null, empty or blank. Otherwise the custom name remains the
     * same.
     * @param customName the new custom name.
     */
    public void setCustomName(final String customName) {
        if (StringUtils.hasText(customName)) {
            this.customName = customName;
        }
    }

    /**
     * Sets the description to a new description as long as its not null.
     * @param description the new description.
     */
    public void setDescription(final String description) {
        if (description != null) {
            this.description = description;
        }
    }

    /**
     * Sets the group of this benchmark to a new group.
     * @param group the new group. Cannot be null.
     */
    public void setGroup(@NotNull final BenchmarkGroup group) {
        Objects.requireNonNull(group);
        this.group = group;
    }

    /**
     * Adds a new property to the list of properties of this benchmark.
     * If the list already contains this property or the property is null, no action is taken.
     * @param property the new property.
     */
    public void addProperty(final BenchmarkProperty property) {
        if (property != null) {
            this.properties.add(property);
        }
    }
}
