package pacr.benchmarker.services;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a property of a benchmark.
 */
public class BenchmarkProperty {

    private Collection<Double> results;
    private String resultInterpretation;
    private String unit;
    private String error;

    public BenchmarkProperty() {
        this.results = new ArrayList<>();
        this.resultInterpretation = "";
        this.unit = "";
        this.error = "";
    }

    /**
     * @return the results of this property.
     */
    public Collection<Double> getResults() {
        return results;
    }

    /**
     * @return LESS_IS_BETTER, MORE_IS_BETTER or NEUTRAL.
     */
    public ResultInterpretation getResultInterpretation() {
        try {
            return ResultInterpretation.valueOf(resultInterpretation);
        } catch (IllegalArgumentException | NullPointerException e) {
            // check that result interpretation
            if (!StringUtils.hasText(error)) {
                error = "Unknown result interpretation.";
            }
        }
        return ResultInterpretation.NEUTRAL;
    }

    /**
     * @return the unit of this property.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @return the error message of this property.
     */
    public String getError() {
        return error;
    }
}
