package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents a slimmed down version of a BenchmarkPropertyResult for output. This only contains the statistical values
 * of the original BenchmarkPropertyResult, not all measured data.
 */
public class OutputPropertyResult implements IBenchmarkProperty {
    private String name;
    private String unit;
    private ResultInterpretation interpretation;

    private double mean;
    private double lowerQuartile;
    private double median;
    private double upperQuartile;
    private double standardDeviation;
    private boolean hadLocalError;
    private String errorMessage;

    /**
     * Creates a OutputPropertyResult from a BenchmarkPropertyResult. Copies all statistical data and the associated
     * property.
     * @param result the BenchmarkPropertyResult that this OutputPropertyResult is cloned from. Throws
     *               IllegalArgumentException if result is null.
     */
    public OutputPropertyResult(@NotNull BenchmarkPropertyResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null");
        }

        this.name = result.getName();
        this.unit = result.getUnit();
        this.interpretation = result.getResultInterpretation();

        this.mean = result.getMean();
        this.lowerQuartile = result.getLowerQuartile();
        this.median = result.getMedian();
        this.upperQuartile = result.getUpperQuartile();
        this.standardDeviation = result.getStandardDeviation();
        this.hadLocalError = result.isError();
        this.errorMessage = result.getError();
    }
    /**
     * Gets the mean of all measurements as one result. The individual measurements are not available in a
     * OutputPropertyResult.
     * @return the mean.
     */
    @Override
    public Collection<Double> getResults() {
        Double[] results = { mean };
        return Arrays.asList(results);
    }

    @Override
    public ResultInterpretation getResultInterpretation() {
        return interpretation;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String getError() {
        if (hadLocalError) {
            return errorMessage;
        }
        return null;
    }

    /**
     * Gets the name of the property.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates whether there was an error while measuring this property.
     * @return {@code true} if there was an error, otherwise {@code false}.
     */
    public boolean hadLocalError() {
        return hadLocalError;
    }

    /**
     * Gets the mean of the measurements.
     * @return the mean.
     */
    public double getMean() {
        return mean;
    }

    /**
     * Gets the lower quartile of the measurements.
     * @return the lower quartile.
     */
    public double getLowerQuartile() {
        return lowerQuartile;
    }

    /**
     * Gets the median of the measurements.
     * @return the median.
     */
    public double getMedian() {
        return median;
    }

    /**
     * Gets the upper quartile of the measurements.
     * @return the upper quartile.
     */
    public double getUpperQuartile() {
        return upperQuartile;
    }

    /**
     * Gets the standard deviation of the measurements.
     * @return the standard deviation.
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }
}
