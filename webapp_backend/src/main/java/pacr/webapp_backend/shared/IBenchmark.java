package pacr.webapp_backend.shared;

import java.util.Map;

/**
 * Represents a benchmark. A benchmark consists of multiple properties.
 */
public interface IBenchmark {

    /**
     * @return all properties of the benchmark associated with their name.
     */
    Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties();

}
