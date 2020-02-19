package pacr.webapp_backend.import_export.servies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

/**
 * Represents a property of a benchmark.
 */
@NoArgsConstructor
public class BenchmarkProperty implements IBenchmarkProperty {

    @Getter
    private ResultInterpretation resultInterpretation;

    @Getter
    private String unit;

    private List<Double> results;
    private String error;

    /**
     * Creates a BenchmarkProperty from an IBenchmarkProperty interface.
     *
     * @param property the IBenchmarkProperty which is used to create the BenchmarkProperty.
     */
    public BenchmarkProperty(IBenchmarkProperty property) {
        this.results = new ArrayList<>(property.getResults());
        this.resultInterpretation = property.getResultInterpretation();
        this.unit = property.getUnit();
        this.error = property.getError();
    }

    @Override
    public Collection<Double> getResults() {
        return results;
    }

    @Override
    public String getError() {
        if (!StringUtils.hasText(error)) {
            return null;
        }
        
        return error;
    }
}
