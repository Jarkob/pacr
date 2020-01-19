package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents all measured benchmark data for one commit. This entity is saved in the database.
 */
@Entity(name = "CommitResult")
@Table(name = "commitResult")
public class CommitResult implements IBenchmarkingResult {

    @Id
    private String commitHash;

    private int repositoryId;
    private boolean error;
    private String errorMessage;

    @OneToOne(cascade = CascadeType.ALL)
    private SystemEnvironment systemEnvironment;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BenchmarkResult> benchmarkResults;

    private LocalDateTime entryDate;
    private String comparisonCommitHash;

    /**
     * Creates empty result. Needed for jpa.
     */
    public CommitResult() {
    }

    /**
     * Creates a CommitResult from an IBenchmarkingResult and measurements for benchmarks. Copies error message,
     * commitHash, system environment and the repository from the IBenchmarkingResult.
     * Throws IllegalArgumentException if any parameter is null.
     * @param result the IBenchmarkingResult.
     * @param benchmarkResults the measured data for each benchmark. May be empty.
     * @param repositoryId id of the repository of the commit.
     * @param comparisonCommitHash the hash of the commit this result was compared to. May be null (in this case it is
     *                             implied that no comparison has taken place).
     */
    public CommitResult(@NotNull IBenchmarkingResult result, @NotNull Set<BenchmarkResult> benchmarkResults,
                        int repositoryId, @Nullable String comparisonCommitHash) {
        if (result == null || benchmarkResults == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        if (result.getGlobalError() != null) {
            this.error = true;
            this.errorMessage = result.getGlobalError();
        } else {
            this.error = false;
            this.errorMessage = null;
        }
        this.commitHash = result.getCommitHash();
        this.repositoryId = repositoryId;
        this.systemEnvironment = new SystemEnvironment(result.getSystemEnvironment());
        this.benchmarkResults = benchmarkResults;
        this.entryDate = LocalDateTime.now();
        this.comparisonCommitHash = comparisonCommitHash;
    }

    /**
     * Creates a CommitResult with no error and no comparison. Throws IllegalArgumentException if any input parameter is
     * null.
     * @param commitHash the hash of the measured commit. Throws IllegalArgumentException if it is empty.
     * @param systemEnvironment the system environment of the benchmarks.
     * @param benchmarkResults the measured data for each benchmark.
     * @param repositoryId id of the repository of the commit.
     */
    public CommitResult(@NotNull String commitHash, @NotNull SystemEnvironment systemEnvironment,
                        @NotNull Set<BenchmarkResult> benchmarkResults, int repositoryId) {
        if (commitHash == null || commitHash.isEmpty()) {
            throw new IllegalArgumentException("commit hash cannot be null or empty");
        }
        if (systemEnvironment == null || benchmarkResults == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        this.error = false;
        this.errorMessage = null;
        this.commitHash = commitHash;
        this.repositoryId = repositoryId;
        this.systemEnvironment = systemEnvironment;
        this.benchmarkResults = benchmarkResults;
        this.entryDate = LocalDateTime.now();
        this.comparisonCommitHash = null;
    }

    /**
     * TODO: @Martin
     * @return
     */
    @Override
    public int getRepositoryID() {
        return -1;
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
    public Map<String, BenchmarkResult> getBenchmarks() {
        Map<String, BenchmarkResult> benchmarks = new HashMap<>();

        for (BenchmarkResult benchmarkResult : benchmarkResults) {
            benchmarks.put(benchmarkResult.getName(), benchmarkResult);
        }

        return benchmarks;
    }

    @Override
    public String getGlobalError() {
        if (hasGlobalError()) {
            return errorMessage;
        }
        return null;
    }

    /**
     * @return Gets the id of the repository of the commit.
     */
    public int getRepositoryId() {
        return repositoryId;
    }

    /**
     * @return the entry date into the system of the result.
     */
    public LocalDateTime getEntryDate() {
        return entryDate;
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
    public boolean hasGlobalError() {
        return error;
    }

    /**
     * @return The hash of the commit that was used for comparison. May be null if no comparison has taken place.
     */
    public String getComparisonCommitHash() {
        return comparisonCommitHash;
    }

    /**
     * Removes the given benchmark result from this commit result.
     * @param benchmarkResult the benchmark result.
     */
    public void removeBenchmarkResult(BenchmarkResult benchmarkResult) {
        benchmarkResults.remove(benchmarkResult);
    }
}
