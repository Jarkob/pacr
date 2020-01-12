package pacr.webapp_backend.result_management;

import java.util.Arrays;
import java.util.Collection;

/**
 * Represents a slimmed down version of a BenchmarkPropertyResult for Output. This only contains the statistical values
 * of the original BenchmarkPropertyResult, not all measured data.
 */
public class SimplePropertyResult implements IOutputPropertyResult {
    private BenchmarkProperty property;
    private double mean;
    private double lowerQuartile;
    private double median;
    private double upperQuartile;
    private double standardDeviation;
    private boolean hadLocalError;
    private String errorMessage;

    /**
     * Creates a SimplePropertyResult from a BenchmarkPropertyResult. Copies all statistical data and the associated
     * property.
     * @param result the BenchmarkPropertyResult that this SimplePropertyResult is cloned from.
     */
    SimplePropertyResult(BenchmarkPropertyResult result) {
        this.property = result.getProperty();
        this.mean = result.getMean();
        this.lowerQuartile = result.getLowerQuartile();
        this.median = result.getMedian();
        this.upperQuartile = result.getUpperQuartile();
        this.standardDeviation = result.getStandardDeviation();
        this.hadLocalError = result.hadLocalError();
        this.errorMessage = result.getError();
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public boolean hadLocalError() {
        return hadLocalError;
    }

    @Override
    public Benchmark getBenchmark() {
        return property.getBenchmark();
    }

    @Override
    public double getMean() {
        return mean;
    }

    @Override
    public double getLowerQuartile() {
        return lowerQuartile;
    }

    @Override
    public double getMedian() {
        return median;
    }

    @Override
    public double getUpperQuartile() {
        return upperQuartile;
    }

    @Override
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Gets the mean of all measurements as one result. The individual measurements are not available in a
     * SimplePropertyResult.
     * @return the mean.
     */
    @Override
    public Collection<Double> getResults() {
        Double[] results = { mean };
        return Arrays.asList(results);
    }

    @Override
    public String getResultInterpretation() {
        return property.getInterpretation();
    }

    @Override
    public String getUnit() {
        return property.getUnit();
    }

    @Override
    public String getError() {
        return errorMessage;
    }
}
