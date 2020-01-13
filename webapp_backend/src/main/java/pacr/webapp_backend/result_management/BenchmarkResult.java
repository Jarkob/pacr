package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all measured data of properties for a benchmark. This entity is saved in the database.
 */
public class BenchmarkResult implements IBenchmark {

    private int id;
    private BenchmarkPropertyResult[] propertyResults;
    private Benchmark benchmark;

    /**
     * Creates a BenchmarkResult from an array of property results with the measured data and a benchmark.
     * @param propertyResults the measured data for properties.
     * @param benchmark the benchmark.
     */
    BenchmarkResult(BenchmarkPropertyResult[] propertyResults, Benchmark benchmark) {
        this.propertyResults = propertyResults;
        this.benchmark = benchmark;
    }

    @Override
    public Map<String, IBenchmarkProperty> getBenchmarkProperties() {
        Map<String, IBenchmarkProperty> properties = new HashMap<>();
        for (BenchmarkPropertyResult propertyResult : propertyResults) {
            properties.put(propertyResult.getName(), propertyResult);
        }
        return properties;
    }

    /**
     * Gets all measured data for the properties in an iterable.
     * @return the iterable property results.
     */
    Iterable<BenchmarkPropertyResult> getPropertiesIterable() {
        return Arrays.asList(propertyResults);
    }

    /**
     * Gets the original name of the benchmark.
     * @return the name.
     */
    String getName() {
        return benchmark.getBenchmarkName();
    }

    /**
     * Gets the benchmark that was executed for the measurements.
     * @return the benchmark.
     */
    Benchmark getBenchmark() {
        return benchmark;
    }
}
