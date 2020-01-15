package pacr.webapp_backend.result_management;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the measured data of one property for one commit or an error message for a property or an entire commit.
 * Contains statistical analysis for the data.
 */
class BenchmarkPropertyResult {
    private int id;
    private Double[] results;
    private double mean;
    private double lowerQuartile;
    private double median;
    private double upperQuartile;
    private double standardDeviation;
    private String commitHash;
    private boolean hadLocalError;
    private boolean hadGlobalError;
    private String errorMessage;
    private BenchmarkProperty property;

    /**
     * Creates a BenchmarkPropertyResult for a set of results of a BenchmarkProperty without any errors.
     * Calculates mean, quantiles and standard deviation for the given results.
     * If the given results iterable is empty, the method throws an IllegalArgumentException.
     *
     * @param commitHash the hash of the commit that the results were measured on.
     * @param results the measured results.
     * @param property the property of a benchmark that was measured.
     */
    BenchmarkPropertyResult(String commitHash, Iterable<Double> results, BenchmarkProperty property) {
        Iterator<Double> resultsIterator = results.iterator();
        LinkedList<Double> resultsList = new LinkedList<>();
        while (resultsIterator.hasNext()) {
            resultsList.add(resultsIterator.next());
        }
        this.results = resultsList.toArray(new Double[0]);
        if (this.results.length == 0) {
            throw new IllegalArgumentException();
        }
        this.mean = this.getMeanFromResults();
        this.lowerQuartile = this.getQuantileFromResults(0.25);
        this.median = this.getQuantileFromResults(0.5);
        this.upperQuartile = this.getQuantileFromResults(0.75);
        this.standardDeviation = this.getStandardDeviationFromResults();
        this.commitHash = commitHash;
        this.hadLocalError = false;
        this.hadGlobalError = false;
        this.errorMessage = "";
        this.property = property;
    }

    /**
     * Creates a BenchmarkPropertyResult for a single BenchmarkProperty that could not be properly measured.
     * No results or statistical values are saved.
     *
     * @param commitHash the hash of the commit whose benchmarking caused this error.
     * @param errorMessage an error message.
     * @param property the BenchmarkProperty where this error occurred.
     */
    BenchmarkPropertyResult(String commitHash, String errorMessage, BenchmarkProperty property) {
        this.commitHash = commitHash;
        this.hadLocalError = true;
        this.hadGlobalError = false;
        this.errorMessage = errorMessage;
        this.property = property;
    }

    /**
     * Creates a BenchmarkPropertyResult for a commit that could not be benchmarked in general.
     * No results or statistical values are saved.
     * No BenchmarkProperty is associated with this BenchmarkPropertyResult.
     *
     * @param commitHash the hash of the commit that could not be benchmarked.
     * @param errorMessage an error message.
     */
    BenchmarkPropertyResult(String commitHash, String errorMessage) {
        this.commitHash = commitHash;
        this.hadLocalError = false;
        this.hadGlobalError = true;
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the unique id of this BenchmarkPropertyResult.
     * @return the id.
     */
    int getId() {
        return id;
    }

    /**
     * Gets all saved results as an iterable.
     * @return the iterable results.
     */
    Iterable<Double> getResults() {
        return Arrays.asList(results);
    }

    /**
     * Gets the mean of the saved results.
     * @return the mean.
     */
    double getMean() {
        return mean;
    }

    /**
     * Gets the lower quartile of the saved results.
     * @return the lower quartile.
     */
    double getLowerQuartile() {
        return lowerQuartile;
    }

    /**
     * Gets the median of the saved results.
     * @return the median.
     */
    double getMedian() {
        return median;
    }

    /**
     * Gets the upper quartile of the saved results.
     * @return the upper quartile.
     */
    double getUpperQuartile() {
        return upperQuartile;
    }

    /**
     * Gets the standard deviation of the saved results.
     * @return the standard Deviation.
     */
    double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Gets the commit hash of the commit these results were measured on.
     * @return the commit hash.
     */
    String getCommitHash() {
        return commitHash;
    }

    /**
     * Indicates whether an error occurred while the BenchmarkProperty of this BenchmarkPropertyResult was measured.
     * @return true, if such an error occurred. otherwise false.
     */
    boolean hadLocalError() {
        return hadLocalError;
    }

    /**
     * Indicates whether a general error occured while benchmarking the commit of this BenchmarkPropertyResult.
     * @return true, if such an error occurred. otherwise false.
     */
    boolean hadGlobalError() {
        return hadGlobalError;
    }

    /**
     * Gets the error message if this BenchmarkPropertyResult had a local or global error. Otherwise an empty String is
     * returned.
     * @return the error message.
     */
    String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the property that is associated with this BenchmarkPropertyResult.
     * @return the property.
     */
    BenchmarkProperty getProperty() {
        return property;
    }

    private double getQuantileFromResults(double p) {
        if (this.results.length == 0) {
            return -1;
        }

        List<Double> resultsList = Arrays.asList(results);
        Collections.sort(resultsList);

        double index = this.results.length * p;

        if (index == Math.ceil(index)) {
            return (resultsList.get((int) index - 1) + resultsList.get((int) index)) / 2;
        } else {
            return resultsList.get((int) Math.floor(index));
        }
    }

    private double getMeanFromResults() {
        if (this.results.length == 0) {
            return -1;
        }
        double total = 0;
        for (double result : this.results) {
            total += result;
        }
        return total / this.results.length;
    }

    private double getStandardDeviationFromResults() {
        if (this.results.length == 0) {
            return -1;
        }
        double mean = this.getMeanFromResults();
        double sumOfResultsMinusMeanSquared = 0;
        for (double result : this.results) {
            sumOfResultsMinusMeanSquared += Math.pow(result - mean, 2);
        }
        return Math.sqrt(sumOfResultsMinusMeanSquared / this.results.length);
    }
}
