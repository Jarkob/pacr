package pacr.webapp_backend.result_management;


import pacr.webapp_backend.shared.IBenchmarkProperty;

import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the measured data of one property or an error message for a property.
 * Contains statistical analysis for the data.
 * This entity is saved in the database.
 */
class BenchmarkPropertyResult implements IBenchmarkProperty {
    private int id;
    private Double[] measurements;
    private double mean;
    private double lowerQuartile;
    private double median;
    private double upperQuartile;
    private double standardDeviation;
    private boolean error;
    private String errorMessage;
    private BenchmarkProperty property;

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
        this.measurements = measurement.getResults().toArray(new Double[0]);
        this.mean = this.getMeanFromResults();
        this.lowerQuartile = this.getQuantileFromResults(0.25);
        this.median = this.getQuantileFromResults(0.5);
        this.upperQuartile = this.getQuantileFromResults(0.75);
        this.standardDeviation = this.getStandardDeviationFromResults();
    }

    /**
     * Creates a BenchmarkPropertyResult directly from measurements. In this case it is assumed there is no error or
     * error message for this property.
     * @param measurements the measurements.
     * @param property the property of a benchmark that was measured.
     */
    BenchmarkPropertyResult(Double[] measurements, BenchmarkProperty property) {
        this.property = property;
        this.error = false;
        this.errorMessage = null;
        this.measurements = measurements;
        this.mean = this.getMeanFromResults();
        this.lowerQuartile = this.getQuantileFromResults(0.25);
        this.median = this.getQuantileFromResults(0.5);
        this.upperQuartile = this.getQuantileFromResults(0.75);
        this.standardDeviation = this.getStandardDeviationFromResults();
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
        return Arrays.asList(measurements);
    }

    @Override
    public String getError() {
        return errorMessage;
    }

    @Override
    public String getUnit() {
        return property.getUnit();
    }

    @Override
    public String getResultInterpretation() {
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

    // todo remove?
    Benchmark getBenchmark() {
        return property.getBenchmark();
    }

    /**
     * Gets the property that is associated with this BenchmarkPropertyResult.
     * @return the property.
     */
    BenchmarkProperty getProperty() {
        return property;
    }

    private double getQuantileFromResults(double p) {
        if (this.measurements.length == 0) {
            return -1;
        }

        List<Double> resultsList = Arrays.asList(measurements);
        Collections.sort(resultsList);

        double index = this.measurements.length * p;

        if (index == Math.ceil(index)) {
            return (resultsList.get((int) index - 1) + resultsList.get((int) index)) / 2;
        } else {
            return resultsList.get((int) Math.floor(index));
        }
    }

    private double getMeanFromResults() {
        if (this.measurements.length == 0) {
            return -1;
        }
        double total = 0;
        for (double result : this.measurements) {
            total += result;
        }
        return total / this.measurements.length;
    }

    private double getStandardDeviationFromResults() {
        if (this.measurements.length == 0) {
            return -1;
        }
        double mean = this.getMeanFromResults();
        double sumOfResultsMinusMeanSquared = 0;
        for (double result : this.measurements) {
            sumOfResultsMinusMeanSquared += Math.pow(result - mean, 2);
        }
        return Math.sqrt(sumOfResultsMinusMeanSquared / this.measurements.length);
    }
}
