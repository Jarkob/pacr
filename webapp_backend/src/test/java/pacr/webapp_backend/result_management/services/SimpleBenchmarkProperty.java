package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Collection;

public class SimpleBenchmarkProperty implements IBenchmarkProperty {

    private Collection<Double> results;
    private ResultInterpretation resultInterpretation;
    private String unit;
    private String error;

    public SimpleBenchmarkProperty(Collection<Double> results, ResultInterpretation resultInterpretation, String unit,
                                   String error) {
        this.results = results;
        this.resultInterpretation = resultInterpretation;
        this.unit = unit;
        this.error = error;
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
}
