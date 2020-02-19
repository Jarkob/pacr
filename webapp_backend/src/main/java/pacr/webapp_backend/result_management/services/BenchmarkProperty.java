package pacr.webapp_backend.result_management.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
@Table(name = "benchmark_property")
@Getter
@Setter
public class BenchmarkProperty {

    @Id
    @GeneratedValue
    private int id;

    @Setter(AccessLevel.NONE)
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
