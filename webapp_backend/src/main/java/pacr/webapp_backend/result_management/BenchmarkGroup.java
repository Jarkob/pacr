package pacr.webapp_backend.result_management;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
     * @param name the name.
     */
    public BenchmarkGroup(String name) {
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
     * Sets the name of this group to a new name.
     * @param name the new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public List<Benchmark> getBenchmarks() {
        return benchmarkList;
    }

    public void addBenchmark(Benchmark benchmark) {
        if (!benchmarkList.contains(benchmark)) {
            benchmarkList.add(benchmark);
        }
    }

    public void removeBenchmark(Benchmark benchmark) {
        benchmarkList.remove(benchmark);
    }
}
