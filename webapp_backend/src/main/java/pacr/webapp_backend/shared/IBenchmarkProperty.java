package pacr.webapp_backend.shared;

import java.util.Collection;

/**
 * Represents a property of a benchmark.
 */
public interface IBenchmarkProperty {

    /**
     * @return a list of all measurements.
     */
    Collection<Double> getResults();

    /**
     * @return how the results should be interpreted.
     */
    ResultInterpretation getResultInterpretation();

    /**
     * @return the measurements unit of the results.
     */
    String getUnit();

    /**
     * @return an error message if an error occurred. otherwise returns null.
     */
    String getError();

}
