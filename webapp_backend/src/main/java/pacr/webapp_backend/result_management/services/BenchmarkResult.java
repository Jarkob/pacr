package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.IBenchmark;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents all measured data of properties for a benchmark. This entity is saved in the database.
 */
@Entity
@Table(name = "benchmark_result")
public class BenchmarkResult implements IBenchmark {

    @Id
    @GeneratedValue
    private int id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BenchmarkPropertyResult> propertyResults;

    @ManyToOne
    @JoinColumn(name = "benchmark_id")
    private Benchmark benchmark;

    /**
     * Creates empty benchmark result. Needed for jpa.
     */
    public BenchmarkResult() {
    }

    /**
     * Creates a BenchmarkResult from an array of property results with the measured data and a benchmark.
     * @param propertyResults the measured data for properties. Throws IllegalArgumentException if it is null or empty.
     * @param benchmark the benchmark. Throws IllegalArgumentException if it is null.
     */
    public BenchmarkResult(@NotNull Set<BenchmarkPropertyResult> propertyResults, @NotNull Benchmark benchmark) {
        Objects.requireNonNull(propertyResults);
        Objects.requireNonNull(benchmark);

        if (propertyResults.isEmpty()) {
            throw new IllegalArgumentException("propertyResults cannot be empty");
        }

        this.propertyResults = propertyResults;
        this.benchmark = benchmark;
    }

    @Override
    public Map<String, BenchmarkPropertyResult> getBenchmarkProperties() {
        Map<String, BenchmarkPropertyResult> properties = new HashMap<>();

        for (BenchmarkPropertyResult propertyResult : propertyResults) {
            properties.put(propertyResult.getName(), propertyResult);
        }

        return properties;
    }

    /**
     * Gets all measured data for the properties in an iterable.
     * @return the iterable property results.
     */
    public Iterable<BenchmarkPropertyResult> getPropertiesIterable() {
        return propertyResults;
    }

    /**
     * Gets the original name of the benchmark.
     * @return the name.
     */
    public String getName() {
        return benchmark.getOriginalName();
    }

    /**
     * Gets the benchmark that was executed for the measurements.
     * @return the benchmark.
     */
    public Benchmark getBenchmark() {
        return benchmark;
    }
}
