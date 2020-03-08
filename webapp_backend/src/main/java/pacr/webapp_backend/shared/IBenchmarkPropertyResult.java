package pacr.webapp_backend.shared;

/**
 * Represents a benchmark property result.
 */
public interface IBenchmarkPropertyResult extends IBenchmarkProperty {

    /**
     * @return The median of the result
     */
    double getMedian();

    /**
     * @return The name of the benchmark property
     */
    String getName();
}
