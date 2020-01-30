package pacr.webapp_backend.result_management.services;

import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.ResultInterpretation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    /**
     * Creates empty property. Needed for jpa.
     */
    public BenchmarkProperty() {
    }

    /**
     * Creates a BenchmarkProperty with a name, a unit and an interpretation.
     *
     * @param name the name. Cannot be null, empty or blank.
     * @param unit the unit. Cannot be null.
     * @param interpretation the interpretation. Cannot be null.
     */
    public BenchmarkProperty(String name, String unit, ResultInterpretation interpretation) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name cannot be null, empty or blank");
        }
        Objects.requireNonNull(unit);
        Objects.requireNonNull(interpretation);

        this.name = name;
        this.unit = unit;
        this.interpretation = interpretation;
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
                && interpretation == property.getInterpretation();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unit, interpretation);
    }
}
