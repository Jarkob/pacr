package pacr.webapp_backend.result_management;

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
@Table(name = "benchmarkGroup")
public class BenchmarkGroup {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    /**
     * Creates empty group. Needed for jpa.
     */
    public BenchmarkGroup() {
    }

    /**
     * Creates a new BenchmarkGroup with a name.
     * @param name the name. Cannot be null, empty or blank (throws IllegalArgumentException).
     */
    public BenchmarkGroup(@NotNull String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null, empty or blank.");
        }
        this.name = name;
    }

    /**
     * Gets the unique id of this group.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of this group.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this group to a new name as long as its not null, empty or blank. Otherwise the name remains
     * the same.
     * @param name the new name.
     */
    public void setName(String name) {
        if (name != null && !name.isEmpty() && !name.isBlank()) {
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BenchmarkGroup group = (BenchmarkGroup) obj;
        return id == group.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }
}
