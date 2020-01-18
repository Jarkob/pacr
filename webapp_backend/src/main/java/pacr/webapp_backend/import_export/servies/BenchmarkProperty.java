package pacr.webapp_backend.import_export.servies;

import java.util.Collection;
import java.util.List;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

/**
 * Represents a property of a benchmark.
 */
public class BenchmarkProperty implements IBenchmarkProperty {

    private List<Double> results;
    private ResultInterpretation resultInterpretation;
    private String unit;
    private String error;

    /**
     * Creates an empty BenchmarkProperty.
     *
     * Needed for Spring to work.
     */
    public BenchmarkProperty() {
    }

    @Override
    public Collection<Double> getResults() {
        return results;
    }

    @Override
    public ResultInterpretation getResultInterpretation() {
        return resultInterpretation;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String getError() {
        if (error.isEmpty() || error.isBlank()) {
            return null;
        }
        
        return error;
    }
}
