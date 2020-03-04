package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple IBenchmark implementation for testing purposes.
 */
public class SimpleBenchmark implements IBenchmark {

    public static final String PROPERTY_NAME = "property";

    private Map<String, IBenchmarkProperty> benchmarkProperties;

    /**
     * Creates SimpleBenchmark with properties.
     * @param benchmarkProperties the properties.
     */
    public SimpleBenchmark(final Map<String, IBenchmarkProperty> benchmarkProperties) {
        this.benchmarkProperties = benchmarkProperties;
    }

    /**
     * Creates SimpleBenchmark with default configuration.
     */
    public SimpleBenchmark() {
        final HashMap<String, IBenchmarkProperty> properties = new HashMap<>();
        properties.put(PROPERTY_NAME, new SimpleBenchmarkProperty());
        this.benchmarkProperties = properties;
    }

    @Override
    public Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties() {
        return benchmarkProperties;
    }

    /**
     * Adds a property to this benchmark.
     * @param name name of the property.
     * @param property the property.
     */
    public void addProperty(final String name, final IBenchmarkProperty property) {
        benchmarkProperties.put(name, property);
    }

    public SimpleBenchmarkProperty getProperty(final String name) {
        return (SimpleBenchmarkProperty) benchmarkProperties.get(name);
    }
}
