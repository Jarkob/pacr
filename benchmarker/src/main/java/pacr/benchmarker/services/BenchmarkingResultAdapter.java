package pacr.benchmarker.services;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for BenchmarkingResult.
 *
 * @author Pavel Zwerschke
 */
@Getter @Setter
public class BenchmarkingResultAdapter {

    private Map<String, Benchmark> benchmarks;
    private String error;

    /**
     * Creates a new instance of BenchmarkingResultAdapter.
     * Contains an empty HashMap and an empty error string.
     */
    public BenchmarkingResultAdapter() {
        this.benchmarks = new HashMap<>();
        this.error = "";
    }
}
