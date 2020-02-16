package pacr.webapp_backend.result_management.services;

import java.util.HashSet;
import java.util.Objects;

import org.springframework.lang.Nullable;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Table(name = "commit_result")
public class CommitResult implements IBenchmarkingResult {

    private static final int MAX_STRING_LENGTH = 2000;

    @Id
    private String commitHash;

    private int repositoryID;
    private boolean error;

    @Column(length = MAX_STRING_LENGTH)
    private String errorMessage;

    @OneToOne(cascade = CascadeType.ALL)
    private SystemEnvironment systemEnvironment;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BenchmarkResult> benchmarkResults;

    private LocalDateTime entryDate;
    private LocalDateTime commitDate;
    private String comparisonCommitHash;

    private boolean significant;

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
     * @param repositoryID id of the repository of the commit.
     * @param commitDate the commit date of the commit. Cannot be null.
     * @param comparisonCommitHash the hash of the commit this result was compared to. May be null (in this case it is
     *                             implied that no comparison has taken place).
     */
    public CommitResult(@NotNull IBenchmarkingResult result, int repositoryID, @NotNull LocalDateTime commitDate,
                        @Nullable String comparisonCommitHash) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(commitDate);

        if (result.getGlobalError() != null) {
            this.error = true;
            this.errorMessage = result.getGlobalError();

            if (this.errorMessage.length() > MAX_STRING_LENGTH) {
                this.errorMessage = this.errorMessage.substring(0, MAX_STRING_LENGTH);
            }
        } else {
            this.error = false;
            this.errorMessage = null;
        }
        this.commitHash = result.getCommitHash();
        this.repositoryID = repositoryID;
        this.systemEnvironment = new SystemEnvironment(result.getSystemEnvironment());
        this.benchmarkResults = new HashSet<>();
        this.entryDate = LocalDateTime.now();
        this.commitDate = commitDate;
        this.comparisonCommitHash = comparisonCommitHash;
    }

    @Override
    public int getRepositoryID() {
        return repositoryID;
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
     * @return the entry date into the system of the result.
     */
    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    /**
     * Gets all the measurements for each benchmark.
     * @return the BenchmarkResults.
     */
    public Set<BenchmarkResult> getBenchmarkResults() {
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

    /**
     * @param error {@code true} if there was an error with this result, otherwise {@code false}
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * @return the commit date of the commit.
     */
    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    /**
     * @param benchmarkResult the result for a benchmark is added to the results of this commit result.
     */
    public void addBenchmarkResult(@NotNull BenchmarkResult benchmarkResult) {
        Objects.requireNonNull(benchmarkResult);

        benchmarkResults.add(benchmarkResult);
    }

    /**
     * @param significant sets whether this commit result is significant compared to the previous result.
     */
    public void setSignificant(boolean significant) {
        this.significant = significant;
    }

    /**
     * @return {@code true} if this result is significant compared to the previous result, otherwise {@code false}.
     */
    public boolean isSignificant() {
        return significant;
    }

    /**
     * @param errorMessage the error message if there was an error with this result. May be null if there was no error.
     */
    public void setErrorMessage(@Nullable String errorMessage) {
        if (errorMessage != null && errorMessage.length() > MAX_STRING_LENGTH) {
            this.errorMessage = errorMessage.substring(0, MAX_STRING_LENGTH);
        } else {
            this.errorMessage = errorMessage;
        }
    }
}
