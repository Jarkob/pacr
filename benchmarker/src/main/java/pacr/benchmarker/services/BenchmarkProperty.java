package pacr.benchmarker.services;

import java.util.Collection;

public class BenchmarkProperty {

    private Collection<Double> results;
    private String resultInterpretation;
    private String unit;
    private String error;

    public Collection<Double> getResults() {
        return results;
    }

    public ResultInterpretation getResultInterpretation() {
        try {
            return ResultInterpretation.valueOf(resultInterpretation);
        } catch (IllegalArgumentException e) {
            error = "Unknown result interpretation.";
        }
        return null;
    }

    public String getUnit() {
        return unit;
    }

    public String getError() {
        return error;
    }
}
