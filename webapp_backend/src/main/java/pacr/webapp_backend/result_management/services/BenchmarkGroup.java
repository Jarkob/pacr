package pacr.webapp_backend.result_management.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Represents a group of benchmarks.
 * This entity is saved in the database.
 */
@Entity(name = "BenchmarkGroup")
@Table(name = "benchmark_group")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class BenchmarkGroup {

    /**
     * the name of the standard group.
     */
    public static final String STANDARD_GROUP_NAME = "Other";

    private static final int MAX_GROUP_AMOUNT = 500;

    @Id
    @GeneratedValue
    private int id;

    private boolean standardGroup;

    @Column(length = MAX_GROUP_AMOUNT)
    private String name;

    /**
     * Creates a new BenchmarkGroup with a name.
     * @param name the name. Cannot be null, empty or blank (throws IllegalArgumentException).
     */
    public BenchmarkGroup(@NotNull final String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank.");
        }
        this.name = name;
    }

    /**
     * Sets this to the standard group.
     */
    public void setToStandardGroup() {
        this.standardGroup = true;
    }

    /**
     * Sets the name of this group to a new name as long as its not null, empty or blank. Otherwise the name remains
     * the same.
     * @param name the new name.
     */
    public void setName(final String name) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
    }
}
