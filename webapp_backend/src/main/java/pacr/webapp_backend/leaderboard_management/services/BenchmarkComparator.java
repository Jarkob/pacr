package pacr.webapp_backend.leaderboard_management.services;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkPropertyResult;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Comparator;

/**
 * A comparator for comparing benchmarks with each other, regarding a certain benchmark property.
 */
public class BenchmarkComparator implements Comparator<IBenchmark> {

    private final String benchmarkPropertyName;

    /**
     * Creates a new benchmark comparator
     * @param benchmarkPropertyName the name of the benchmark property, which should be compared by.
     */
    public BenchmarkComparator(String benchmarkPropertyName) {
        this.benchmarkPropertyName = benchmarkPropertyName;
    }

    @Override
    public int compare(IBenchmark o1, IBenchmark o2) {
        ResultInterpretation ri = o1.getBenchmarkProperties().get(benchmarkPropertyName).getResultInterpretation();

        double firstResult =
                //This might not work in the future
                ((IBenchmarkPropertyResult) o1.getBenchmarkProperties().get(benchmarkPropertyName)).getMedian();

        double secondResult = (Double)
                ((IBenchmarkPropertyResult) o2.getBenchmarkProperties().get(benchmarkPropertyName)).getMedian();


        if (ri == ResultInterpretation.LESS_IS_BETTER) {
            if (firstResult < secondResult) {
                return -1;
            } else if (firstResult == secondResult) {
                return 0;
            }
            return -1;
        } else if (ri == ResultInterpretation.MORE_IS_BETTER) {
            if (firstResult > secondResult) {
                return -1;
            } else if (firstResult == secondResult) {
                return 0;
            }
            return -1;
        }

        return 0;
    }
}
