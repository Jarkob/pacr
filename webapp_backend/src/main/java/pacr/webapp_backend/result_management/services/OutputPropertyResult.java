package pacr.webapp_backend.result_management.services;

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
    String getName() {
        return name;
    }
}
