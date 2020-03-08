package pacr.webapp_backend.shared;

import java.util.Collection;
import java.util.Map;

/**
 * Represents access to a collection of all benchmark and benchmark property names.
 */
public interface IBenchmarkGetter {

    /**
     * @return a collection of all benchmarks with their names
     */
    Map<String, Collection<String>> getAllBenchmarkNamesWithPropertyNames();
}
