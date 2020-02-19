package pacr.webapp_backend.result_management.services;

import lombok.Setter;
import org.springframework.lang.Nullable;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Simple IBenchmarkProperty implementation for testing purposes.
 */
public class SimpleBenchmarkProperty implements IBenchmarkProperty {

    public static final Double MEASUREMENT = 12d;
    public static final String UNIT = "unit";
    public static final String NO_ERROR = null;

    private Collection<Double> results;
    @Setter
    private ResultInterpretation resultInterpretation;
    private String unit;
    private String error;

    /**
     * Creates a SimpleBenchmarkProperty with given attributes.
     * @param results the result data.
     * @param resultInterpretation the interpretation of the results.
     * @param unit the unit of the results.
     * @param error an error message.
     */
    public SimpleBenchmarkProperty(Collection<Double> results, ResultInterpretation resultInterpretation, String unit,
                                   String error) {
        this.results = results;
        this.resultInterpretation = resultInterpretation;
        this.unit = unit;
        this.error = error;
    }

    /**
     * Creates a SimpleBenchmarkProperty with a default configuration.
     */
    public SimpleBenchmarkProperty() {
        LinkedList<Double> results = new LinkedList<>();
        results.add(MEASUREMENT);
        this.results = results;
        this.resultInterpretation = ResultInterpretation.LESS_IS_BETTER;
        this.unit = UNIT;
        this.error = NO_ERROR;
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
        return error;
    }

    /**
     * @param error Sets the error message. May be null. This implies there was no error.
     */
    public void setError(@Nullable String error) {
        this.error = error;
    }

    public void setResults(Collection<Double> results) {
        this.results = results;
    }
}
