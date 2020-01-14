package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents all measured benchmark data for one commit. This entity is saved in the database.
 */
@Entity(name = "CommitResult")
@Table(name = "commitResult")
public class CommitResult implements IBenchmarkingResult {

    @Id
    private String commitHash;

    private boolean error;
    private String errorMessage;

    @OneToOne(cascade = CascadeType.ALL)
    private SystemEnvironment systemEnvironment;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BenchmarkResult> benchmarkResults;

    /**
     * Creates empty result. Needed for jpa.
     */
    CommitResult() {
    }

    /**
     * Creates a CommitResult from an IBenchmarkingResult and measurements for benchmarks. Copies error message,
     * commitHash, system environment and the repository from the IBenchmarkingResult.
     * @param result the IBenchmarkingResult
     * @param benchmarkResults the measured data for each benchmark.
     */
    public CommitResult(IBenchmarkingResult result, List<BenchmarkResult> benchmarkResults) {
        if (result.getGlobalError() != null) {
            this.error = true;
            this.errorMessage = result.getGlobalError();
        } else {
            this.error = false;
            this.errorMessage = null;
        }
        this.commitHash = result.getCommitHash();
        this.systemEnvironment = new SystemEnvironment(result.getSystemEnvironment());
        this.benchmarkResults = benchmarkResults;
    }

    /**
     * Creates a CommitResult with no error.
     * @param commitHash the hash of the measured commit-
     * @param systemEnvironment the system environment of the benchmarks.
     * @param benchmarkResults the measured data for each benchmark.
     */
    public CommitResult(String commitHash, SystemEnvironment systemEnvironment, List<BenchmarkResult> benchmarkResults) {
        this.error = false;
        this.errorMessage = null;
        this.commitHash = commitHash;
        this.systemEnvironment = systemEnvironment;
        this.benchmarkResults = benchmarkResults;
    }

    @Override
    public String getCommitHash() {
        return commitHash;
    }

    @Override
    public ISystemEnvironment getSystemEnvironment() {
        return systemEnvironment;
    }

    @Override
    public Map<String, IBenchmark> getBenchmarks() {
        Map<String, IBenchmark> benchmarks = new HashMap<>();
        for (BenchmarkResult benchmarkResult : benchmarkResults) {
            benchmarks.put(benchmarkResult.getName(), benchmarkResult);
        }
        return benchmarks;
    }

    @Override
    public String getGlobalError() {
        return errorMessage;
    }

    /**
     * Gets an iterable of all the measurements for each benchmark.
     * @return the iterable BenchmarkResults.
     */
    public Iterable<BenchmarkResult> getBenchmarksIterable() {
        return benchmarkResults;
    }

    /**
     * Indicates whether a global error occurred while benchmarking the commit.
     * @return true if an error occurred, otherwise false.
     */
    boolean isError() {
        return error;
    }
}
