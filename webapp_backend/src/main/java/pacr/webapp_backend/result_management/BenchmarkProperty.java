package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.ResultInterpretation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Represents one property of a benchmark that is measured.
 * This entity is saved in the database.
 */
@Entity(name = "BenchmarkProperty")
@Table(name = "benchmarkProperty")
public class BenchmarkProperty {

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String unit;
    private ResultInterpretation interpretation;

    @ManyToOne
    @JoinColumn(name = "benchmark_id")
    private Benchmark benchmark;

    /**
     * Creates empty property. Needed for jpa.
     */
    public BenchmarkProperty() {
    }

    /**
     * Creates a BenchmarkProperty with a name, a unit, an interpretation and the corresponding benchmark.
     * This constructor does not add this property to the benchmark.
     * Throws IllegalArgumentException if one of the parameters is null or the name is empty or blank.
     *
     * @param name the name;
     * @param unit the unit;
     * @param interpretation the interpretation;
     * @param benchmark the corresponding benchmark;
     */
    public BenchmarkProperty(String name, String unit, ResultInterpretation interpretation, Benchmark benchmark) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }
        if (unit == null || interpretation == null || benchmark == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        this.name = name;
        this.unit = unit;
        this.interpretation = interpretation;
        this.benchmark = benchmark;
    }

    /**
     * Gets the unique id of this property.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this property. Sometimes jpa doesn't set this itself for some reason. Only use this if the given
     * id maps to an object in the database.
     * @param id the unique id from the database.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of this property.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the measured unit of this property.
     * @return the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Gets the interpretation (usually "MORE_IS_BETTER", "LESS_IS_BETTER" or "NEUTRAL") of this property.
     * @return the interpretation.
     */
    public ResultInterpretation getInterpretation() {
        return interpretation;
    }

    /**
     * Gets the benchmark that this property is associated with. Does not guarantee that the given benchmark is also
     * associated with this property.
     * @return the benchmark.
     */
    public Benchmark getBenchmark() {
        return benchmark;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BenchmarkProperty property = (BenchmarkProperty) obj;
        return id == property.getId()
                && Objects.equals(name, property.getName())
                && Objects.equals(unit, property.getUnit())
                && interpretation == property.getInterpretation()
                && Objects.equals(benchmark, property.getBenchmark());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unit, interpretation, benchmark);
    }
}
