package pacr.webapp_backend.result_management;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL)
    private List<Benchmark> benchmarkList;

    /**
     * Creates empty group. Needed for jpa.
     */
    BenchmarkGroup() {
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
        this.benchmarkList = new LinkedList<>();
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

    /**
     * Gets all benchmarks of this group.
     * @return the benchmarks.
     */
    public List<Benchmark> getBenchmarks() {
        return benchmarkList;
    }

    /**
     * Adds a benchmark to this group if it wasn't already associated. No action is taken if the given benchmark is
     * null.
     * @param benchmark the benchmark.
     */
    public void addBenchmark(Benchmark benchmark) {
        if (benchmark != null && !benchmarkList.contains(benchmark)) {
            benchmarkList.add(benchmark);
        }
    }

    /**
     * Removes a benchmark from this group. Nothing happens if the benchmark wasn't associated with this group.
     * @param benchmark the benchmark.
     */
    public void removeBenchmark(Benchmark benchmark) {
        benchmarkList.remove(benchmark);
    }
}
