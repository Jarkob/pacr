package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Collection;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

/**
 * Represents a property of a benchmark.
 */
public class BenchmarkProperty implements IBenchmarkProperty {

    private Collection<Double> results;
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
        if (!StringUtils.hasText(error)) {
            return null;
        }
        
        return error;
    }

    /**
     * @param results are the results.
     */
    public void setResults(Collection<Double> results) {
        this.results = results;
    }

    /**
     * @param resultInterpretation is the result interpretation.
     */
    public void setResultInterpretation(ResultInterpretation resultInterpretation) {
        this.resultInterpretation = resultInterpretation;
    }

    /**
     * @param unit is the unit of the benchmark property.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @param error is the error message of the benchmark property.
     */
    public void setError(String error) {
        this.error = error;
    }
}
