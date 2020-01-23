package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import java.util.Map;

public class SimpleBenchmark implements IBenchmark {

    private Map<String, IBenchmarkProperty> benchmarkProperties;

    public SimpleBenchmark(Map<String, IBenchmarkProperty> benchmarkProperties) {
        this.benchmarkProperties = benchmarkProperties;
    }

    @Override
    public Map<String, ? extends IBenchmarkProperty> getBenchmarkProperties() {
        return benchmarkProperties;
    }

    public void addProperty(String name, IBenchmarkProperty property) {
        benchmarkProperties.put(name, property);
    }
}
