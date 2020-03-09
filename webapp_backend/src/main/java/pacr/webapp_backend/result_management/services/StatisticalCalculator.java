package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Calculates statistical information for a given amount of values.
 */
public final class StatisticalCalculator {
    /**
     * This is returned if no calculations could be made on the given input (due to being null, empty, etc.)
     */
    public static final int ERROR_CODE = -1;

    private static final double SIGNIFICANCE_FACTOR = 3.0d;

    private static final double DELTA = 1.0e-8;

    /**
     * No instance of this class can be created because all methods are static.
     */
    private StatisticalCalculator() {
    }

    /**
     * Calculates the p-th quantile of the given values. Returns -1 if the given list is empty or null or if the given
     * quantile is outside 0 to 1.
     * @param p the wanted quantile.
     * @param values the values.
     * @return the calculated quantile.
     */
    static double getQuantile(final double p, @NotNull final List<Double> values) {
        if (values == null || values.isEmpty() || p < 0 || p > 1) {
            return ERROR_CODE;
        }

        final List<Double> resultsList = new LinkedList<>(values);
        Collections.sort(resultsList);

        final double index = values.size() * p;

        if (Math.abs(index - Math.ceil(index)) < DELTA) {
            return (resultsList.get((int) index - 1) + resultsList.get((int) index)) / 2;
        } else {
            return resultsList.get((int) Math.floor(index));
        }
    }

    /**
     * Calculates the mean of the given values. Returns -1 if the given list is empty or null.
     * @param values the values.
     * @return the mean.
     */
    static double getMean(@NotNull final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return ERROR_CODE;
        }
        double total = 0;
        for (final double value : values) {
            total += value;
        }
        return total / values.size();
    }

    /**
     * Calculates the standard deviation from the mean of the given values. Returns -1 if the given list is empty
     * or null.
     * @param values the values.
     * @return the standard deviation.
     */
    static double getStandardDeviation(@NotNull final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return ERROR_CODE;
        }
        final double mean = getMean(values);
        double sumOfResultsMinusMeanSquared = 0;
        for (final double value : values) {
            sumOfResultsMinusMeanSquared += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sumOfResultsMinusMeanSquared / values.size());
    }

    /**
     * Compares the two results and sets the ratio and significance in the result.
     * @param result the result that is compared and whose ratio is set. Cannot be null.
     * @param comparisonResult the result that is used for comparison. This object is not altered. No comparison is done
     *                         if this is null.
     */
    static void compare(@NotNull final BenchmarkPropertyResult result,
                        @Nullable final BenchmarkPropertyResult comparisonResult) {
        Objects.requireNonNull(result);

        if (comparisonResult != null && !result.isError() && !comparisonResult.isError()) {
            if (comparisonResult.getMedian() != 0) {
                // if the comparison result is 0, the default ratio of the result is not changed
                result.setRatio(result.getMedian() / comparisonResult.getMedian());
            }
            result.setCompared(true);

            if (significantChange(result, comparisonResult)) {
                result.setSignificant(true);
            }
        }
    }

    /**
     * A result is considered significant if the median strays at least SIGNIFICANCE_FACTOR standard deviations from the
     * previous result.
     * @param result The first result.
     * @param comparison The result to compare the first one to.
     * @return Whether the change in result was found to be significant.
     */
    private static boolean significantChange(final BenchmarkPropertyResult result,
                                             final BenchmarkPropertyResult comparison) {
        double standardDeviation = result.getStandardDeviation();
        if (comparison.getStandardDeviation() > standardDeviation) {
            standardDeviation = comparison.getStandardDeviation();
        }

        final double insignificanceInterval = SIGNIFICANCE_FACTOR * standardDeviation;

        return Math.abs(result.getMedian() - comparison.getMedian()) > insignificanceInterval;
    }
}
