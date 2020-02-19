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
import java.util.HashSet;
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
     * Creates a BenchmarkResult from a benchmark.
     * @param benchmark the benchmark. Cannot be null.
     */
    public BenchmarkResult(@NotNull Benchmark benchmark) {
        Objects.requireNonNull(benchmark);

        this.propertyResults = new HashSet<>();
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
     * Gets all measured data for the properties.
     * @return the property results.
     */
    public Set<BenchmarkPropertyResult> getPropertyResults() {
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

    /**
     * @param propertyResult adds the property results to the properties of this benchmark.
     */
    public void addPropertyResult(@NotNull BenchmarkPropertyResult propertyResult) {
        Objects.requireNonNull(propertyResult);

        propertyResults.add(propertyResult);
    }
}
