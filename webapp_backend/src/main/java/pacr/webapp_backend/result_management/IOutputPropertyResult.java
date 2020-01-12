package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmarkProperty;

/**
 * Represents a BenchmarkPropertyResult for Output
 */
public interface IOutputPropertyResult extends IBenchmarkProperty {

    /**
     * Gets the name of the property.
     * @return the name
     */
    String getName();

    /**
     * Indicates whether there was an error while measuring this property.
     * @return true if there was an error. otherwise false.
     */
    boolean hadLocalError();

    /**
     * Gets the benchmark of the property.
     * @return the benchmark.
     */
    Benchmark getBenchmark();

    /**
     * Gets the mean of the measurements.
     * @return the mean.
     */
    double getMean();

    /**
     * Gets the lower quartile of the measurements.
     * @return the lower quartile.
     */
    double getLowerQuartile();

    /**
     * Gets the median of the measurements.
     * @return the median.
     */
    double getMedian();

    /**
     * Gets the upper quartile of the measurements.
     * @return the upper quartile.
     */
    double getUpperQuartile();

    /**
     * Gets the standard deviation of the measurements.
     * @return the standard deviation.
     */
    double getStandardDeviation();
}
