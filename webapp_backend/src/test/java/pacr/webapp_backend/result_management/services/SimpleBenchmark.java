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

    public SimpleBenchmark(Map<String, IBenchmarkProperty> benchmarkProperties) {
        this.benchmarkProperties = benchmarkProperties;
    }

    public SimpleBenchmark() {
        HashMap<String, IBenchmarkProperty> properties = new HashMap<>();
        properties.put(PROPERTY_NAME, new SimpleBenchmarkProperty());
        this.benchmarkProperties = properties;
    }

    @Override
    public Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties() {
        return benchmarkProperties;
    }

    public void addProperty(String name, IBenchmarkProperty property) {
        benchmarkProperties.put(name, property);
    }
}
