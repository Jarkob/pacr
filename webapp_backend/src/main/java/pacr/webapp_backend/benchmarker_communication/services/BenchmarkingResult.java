package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmark;

/**
 * Represents a collection of benchmarks that were run for a commit.
 */
@NoArgsConstructor
public class BenchmarkingResult {

    private Map<String, Benchmark> benchmarks;

    @Setter
    private String globalError;

    /**
     * @return a list of benchmarks that were run associated with their name.
     */
    public Map<String, ? extends IBenchmark> getBenchmarks() {
        return benchmarks;
    }

    /**
     * @return an error message if there was a general error. Null is returned if there was no error.
     */
    public String getGlobalError() {
        if (!StringUtils.hasText(globalError)) {
            return null;
        }

        return globalError;
    }

}
