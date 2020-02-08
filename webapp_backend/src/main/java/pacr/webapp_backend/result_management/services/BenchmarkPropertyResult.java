package pacr.webapp_backend.result_management.services;


import org.springframework.lang.Nullable;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the measured data of one property or an error message for a property.
 * Contains statistical analysis for the data.
 * This entity is saved in the database.
 */
@Entity
public class BenchmarkPropertyResult implements IBenchmarkProperty {
    private static final int DEFAULT_RATIO = 1;
    private static final int MAX_STRING_LENGTH = 2000;

    @Id
    @GeneratedValue
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Double> measurements;

    private double mean;
    private double lowerQuartile;
    private double median;
    private double upperQuartile;
    private double standardDeviation;
    private boolean error;

    @Column(length = MAX_STRING_LENGTH)
    private String errorMessage;

    private double ratio;
    private boolean compared;

    @ManyToOne
    @JoinColumn
    private BenchmarkProperty property;

    /**
     * Creates empty property result. Needed for jpa.
     */
    public BenchmarkPropertyResult() {
    }

    /**
     * Creates a BenchmarkPropertyResult from an IBenchmarkProperty and its corresponding BenchmarkProperty object.
     * Calculates mean, quantiles and standard deviation for the given measurements. Assumes that this result has not
     * yet been compared.
     *
     * @param measurement the measurements.
     * @param property the property of a benchmark that was measured.
     */
    public BenchmarkPropertyResult(IBenchmarkProperty measurement, BenchmarkProperty property) {
        this.property = property;
        if (measurement.getError() != null) {
            this.error = true;
            this.errorMessage = measurement.getError();

            if (this.errorMessage.length() > MAX_STRING_LENGTH) {
                this.errorMessage = this.errorMessage.substring(0, MAX_STRING_LENGTH);
            }
        } else {
            this.error = false;
            this.errorMessage = null;
        }
        this.measurements = new LinkedList<>(measurement.getResults());
        this.mean = StatisticalCalculator.getMean(this.measurements);
        this.lowerQuartile = StatisticalCalculator.getQuantile(0.25, this.measurements);
        this.median = StatisticalCalculator.getQuantile(0.5, this.measurements);
        this.upperQuartile = StatisticalCalculator.getQuantile(0.75, this.measurements);
        this.standardDeviation = StatisticalCalculator.getStandardDeviation(this.measurements);

        this.ratio = DEFAULT_RATIO;
        this.compared = false;
    }

    /**
     * Creates a BenchmarkPropertyResult directly from measurements. Assumes that this result has not yet been compared.
     * @param measurements the measurements. Cannot be null.
     * @param property the property of a benchmark that was measured. Cannot be null.
     * @param errorMessage the error message. May be null. In this case no error is assumed.
     */
    public BenchmarkPropertyResult(@NotNull List<Double> measurements, @NotNull BenchmarkProperty property,
                                   @Nullable String errorMessage) {
        Objects.requireNonNull(measurements);
        Objects.requireNonNull(property);

        this.property = property;
        this.error = errorMessage != null;
        this.errorMessage = errorMessage;
        this.measurements = measurements;
        this.mean = StatisticalCalculator.getMean(this.measurements);
        this.lowerQuartile = StatisticalCalculator.getQuantile(0.25, this.measurements);
        this.median = StatisticalCalculator.getQuantile(0.5, this.measurements);
        this.upperQuartile = StatisticalCalculator.getQuantile(0.75, this.measurements);
        this.standardDeviation = StatisticalCalculator.getStandardDeviation(this.measurements);

        this.ratio = DEFAULT_RATIO;
        this.compared = false;
    }

    @Override
    public Collection<Double> getResults() {
        return measurements;
    }

    @Override
    public String getError() {
        if (isError()) {
            return errorMessage;
        }
        return null;
    }

    @Override
    public String getUnit() {
        return property.getUnit();
    }

    @Override
    public ResultInterpretation getResultInterpretation() {
        return property.getInterpretation();
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
    double getLowerQuartile() {
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
    double getUpperQuartile() {
        return upperQuartile;
    }

    /**
     * Gets the standard deviation of the measurements.
     * @return the standard deviation.
     */
    double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Indicates whether an error occurred while benchmarking the property.
     * @return true if such an error occurred. otherwise false.
     */
    public boolean isError() {
        return error;
    }

    /**
     * Gets the name of the property.
     * @return the name.
     */
    public String getName() {
        return property.getName();
    }

    /**
     * @return The ratio of this property result with the property result of the previous commit.
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * @return {@code true} if this property result was compared to another one, otherwise {@code false}
     */
    public boolean isCompared() {
        return compared;
    }

    /**
     * Sets the ratio of this results mean to a result for comparison.
     * @param ratio the ratio between the two means.
     */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     * Sets whether this property result was compared to another result (with a ratio between the means) or not.
     * @param compared the boolean value.
     */
    public void setCompared(boolean compared) {
        this.compared = compared;
    }

    /**
     * @param error {@code true} if there was an error measuring this property, otherwise {@code false}.
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * @param errorMessage the error message if there was an error. May be null if there was no error.
     */
    public void setErrorMessage(@Nullable String errorMessage) {
        if (errorMessage != null && errorMessage.length() > MAX_STRING_LENGTH) {
            this.errorMessage = errorMessage.substring(0, MAX_STRING_LENGTH);
        } else {
            this.errorMessage = errorMessage;
        }
    }
}
