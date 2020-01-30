package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Collection;
import java.util.List;
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
     * TODO
     * @param results
     */
    public void setResults(Collection<Double> results) {
        this.results = results;
    }

    /**
     * TODO
     * @param resultInterpretation
     */
    public void setResultInterpretation(ResultInterpretation resultInterpretation) {
        this.resultInterpretation = resultInterpretation;
    }

    /**
     * TODO
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * TODO
     * @param error
     */
    public void setError(String error) {
        this.error = error;
    }
}
