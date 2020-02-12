package pacr.webapp_backend.result_management.services;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Calculates statistical information for a given amount of values.
 */
public final class StatisticalCalculator {
    /**
     * This is returned if no calculations could be made on the given input (due to being null, empty, etc.)
     */
    public static final int ERROR_CODE = -1;

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
    static double getQuantile(double p, @NotNull List<Double> values) {
        if (values == null || values.isEmpty() || p < 0 || p > 1) {
            return ERROR_CODE;
        }

        List<Double> resultsList = new LinkedList<>(values);
        Collections.sort(resultsList);

        double index = values.size() * p;

        if (index == Math.ceil(index)) {
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
    static double getMean(@NotNull List<Double> values) {
        if (values == null || values.size() == 0) {
            return ERROR_CODE;
        }
        double total = 0;
        for (double value : values) {
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
    static double getStandardDeviation(@NotNull List<Double> values) {
        if (values == null || values.size() == 0) {
            return ERROR_CODE;
        }
        double mean = getMean(values);
        double sumOfResultsMinusMeanSquared = 0;
        for (double value : values) {
            sumOfResultsMinusMeanSquared += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sumOfResultsMinusMeanSquared / values.size());
    }
}
