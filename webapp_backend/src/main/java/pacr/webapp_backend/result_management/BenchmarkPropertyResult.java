package pacr.webapp_backend.result_management;


import pacr.webapp_backend.result_management.services.StatisticalCalculator;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the measured data of one property or an error message for a property.
 * Contains statistical analysis for the data.
 * This entity is saved in the database.
 */
@Entity
public class BenchmarkPropertyResult implements IBenchmarkProperty {
    @Id
    @GeneratedValue
    private int id;

    @ElementCollection
    private List<Double> measurements;

    private double mean;
    private double lowerQuartile;
    private double median;
    private double upperQuartile;
    private double standardDeviation;
    private boolean error;
    private String errorMessage;

    @ManyToOne
    private BenchmarkProperty property;

    /**
     * Creates empty property result. Needed for jpa.
     */
    public BenchmarkPropertyResult() {
    }

    /**
     * Creates a BenchmarkPropertyResult from an IBenchmarkProperty and its corresponding BenchmarkProperty object.
     * Calculates mean, quantiles and standard deviation for the given measurements.
     *
     * @param measurement the measurements.
     * @param property the property of a benchmark that was measured.
     */
    BenchmarkPropertyResult(IBenchmarkProperty measurement, BenchmarkProperty property) {
        this.property = property;
        if (measurement.getError() != null) {
            this.error = true;
            this.errorMessage = measurement.getError();
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
    }

    /**
     * Creates a BenchmarkPropertyResult directly from measurements. In this case it is assumed there is no error or
     * error message for this property.
     * @param measurements the measurements.
     * @param property the property of a benchmark that was measured.
     */
    BenchmarkPropertyResult(List<Double> measurements, BenchmarkProperty property) {
        this.property = property;
        this.error = false;
        this.errorMessage = null;
        this.measurements = measurements;
        this.mean = StatisticalCalculator.getMean(this.measurements);
        this.lowerQuartile = StatisticalCalculator.getQuantile(0.25, this.measurements);
        this.median = StatisticalCalculator.getQuantile(0.5, this.measurements);
        this.upperQuartile = StatisticalCalculator.getQuantile(0.75, this.measurements);
        this.standardDeviation = StatisticalCalculator.getStandardDeviation(this.measurements);
    }

    /**
     * Gets the unique id of this BenchmarkPropertyResult.
     * @return the id.
     */
    int getId() {
        return id;
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
    double getMean() {
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
    double getMedian() {
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
     * Gets the property that is associated with this BenchmarkPropertyResult.
     * @return the property.
     */
    BenchmarkProperty getProperty() {
        return property;
    }
}
