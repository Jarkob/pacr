package pacr.benchmarker.services;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a property of a benchmark.
 */
public class BenchmarkProperty {

    @Getter
    private Collection<Double> results;
    private String resultInterpretation;
    @Getter
    private String unit;
    @Getter
    private String error;

    public BenchmarkProperty() {
        this.results = new ArrayList<>();
        this.resultInterpretation = "";
        this.unit = "";
        this.error = "";
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
}
