package pacr.webapp_backend.result_management.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

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

    private double ratioToPreviousCommit;
    private boolean compared;

    /**
     * Creates a OutputPropertyResult from a BenchmarkPropertyResult. Copies all statistical data and the associated
     * property.
     * @param result the BenchmarkPropertyResult that this OutputPropertyResult is cloned from. Cannot be null.
     */
    OutputPropertyResult(@NotNull BenchmarkPropertyResult result) {
        Objects.requireNonNull(result);

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

        this.ratioToPreviousCommit = result.getRatio();
        this.compared = result.isCompared();
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

    @Override @JsonIgnore
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
     * @return the interpretation of the measurements.
     */
    public ResultInterpretation getInterpretation() {
        return interpretation;
    }

    /**
     * @return the mean of the measurements.
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return the lower quartile of the measurements.
     */
    public double getLowerQuartile() {
        return lowerQuartile;
    }

    /**
     * @return the median of the measurements.
     */
    public double getMedian() {
        return median;
    }

    /**
     * @return the upper quartile of the measurements.
     */
    public double getUpperQuartile() {
        return upperQuartile;
    }

    /**
     * @return the standard deviation of the measurements.
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * @return {@code true} if there was an error measuring this property, otherwise {@code false}.
     */
    public boolean isHadLocalError() {
        return hadLocalError;
    }

    /**
     * @return the error message if there was an error.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the ratio of the median with the median of the comparison commit.
     */
    public double getRatioToPreviousCommit() {
        return ratioToPreviousCommit;
    }

    /**
     * @return {@code true} if a comparison to a parent commit was done and ratio has been properly set, otherwise
     * {@code false}.
     */
    public boolean isCompared() {
        return compared;
    }

    /**
     * Gets the name of the property.
     * @return the name
     */
    public String getName() {
        return name;
    }
}
