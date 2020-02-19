package pacr.webapp_backend.benchmarker_communication.services;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;

/**
 * Represents the result of a BenchmarkerJob.
 */
@NoArgsConstructor
@Getter
public class JobResult implements IBenchmarkingResult {

    private static final String BENCHMARKING_RESULT_MISSING_ERROR = "Results could not be sent to the server.";

    private long executionTime;
    private String repository;
    private String commitHash;
    private SystemEnvironment systemEnvironment;

    @Setter
    @Getter(AccessLevel.NONE)
    private BenchmarkingResult benchmarkingResult;

    @Override
    public int getRepositoryID() {
        return -1; // the repository id is unknown at this point.
    }

    @Override
    public Map<String, ? extends IBenchmark> getBenchmarks() {
        if (benchmarkingResult == null) {
            return new HashMap<>();
        }

        return benchmarkingResult.getBenchmarks();
    }

    @Override
    public String getGlobalError() {
        if (benchmarkingResult == null) {
            return BENCHMARKING_RESULT_MISSING_ERROR;
        }

        return benchmarkingResult.getGlobalError();
    }

}
