package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.result_management.SystemEnvironment;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class SimpleBenchmarkProperty implements IBenchmarkProperty {

    public static final Double MEASUREMENT = 12d;
    public static final String UNIT = "unit";
    public static final String ERROR = null;

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

    public SimpleBenchmarkProperty() {
        LinkedList<Double> results = new LinkedList<>();
        results.add(MEASUREMENT);
        this.results = results;
        this.resultInterpretation = ResultInterpretation.LESS_IS_BETTER;
        this.unit = UNIT;
        this.error = ERROR;
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
