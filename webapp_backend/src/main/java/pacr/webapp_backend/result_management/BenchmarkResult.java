package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents all measured data of properties for a benchmark. This entity is saved in the database.
 */
@Entity
public class BenchmarkResult implements IBenchmark {

    @Id
    @GeneratedValue
    private int id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BenchmarkPropertyResult> propertyResults;

    @ManyToOne
    private Benchmark benchmark;

    /**
     * Creates empty benchmark result. Needed for jpa.
     */
    public BenchmarkResult() {
    }

    /**
     * Creates a BenchmarkResult from an array of property results with the measured data and a benchmark.
     * @param propertyResults the measured data for properties.
     * @param benchmark the benchmark.
     */
    public BenchmarkResult(List<BenchmarkPropertyResult> propertyResults, Benchmark benchmark) {
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
        return propertyResults;
    }

    /**
     * Gets the original name of the benchmark.
     * @return the name.
     */
    public String getName() {
        return benchmark.getBenchmarkName();
    }

    /**
     * Gets the benchmark that was executed for the measurements.
     * @return the benchmark.
     */
    public Benchmark getBenchmark() {
        return benchmark;
    }
}
