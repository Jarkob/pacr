package pacr.webapp_backend.result_management.services;

import java.util.HashSet;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
@Getter
public class CommitResult implements IBenchmarkingResult {

    private static final int MAX_STRING_LENGTH = 2000;

    @Id
    private String commitHash;

    private int repositoryID;
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.NONE)
    private boolean error;

    @Column(length = MAX_STRING_LENGTH)
    @Getter(AccessLevel.NONE)
    private String errorMessage;

    @OneToOne(cascade = CascadeType.ALL)
    private SystemEnvironment systemEnvironment;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BenchmarkResult> benchmarkResults;

    private LocalDateTime entryDate;
    private LocalDateTime commitDate;

    private String comparisonCommitHash;
    @Setter(AccessLevel.PACKAGE)
    private boolean compared;

    @Setter(AccessLevel.PACKAGE)
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
    public CommitResult(@NotNull final IBenchmarkingResult result, final int repositoryID, @NotNull final LocalDateTime commitDate,
                        @Nullable final String comparisonCommitHash) {
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
    public Map<String, BenchmarkResult> getBenchmarks() {
        final Map<String, BenchmarkResult> benchmarks = new HashMap<>();

        for (final BenchmarkResult benchmarkResult : benchmarkResults) {
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
     * Indicates whether a global error occurred while benchmarking the commit.
     * @return true if an error occurred, otherwise false.
     */
    public boolean hasGlobalError() {
        return error;
    }

    /**
     * @param benchmarkResult the result for a benchmark is added to the results of this commit result.
     */
    public void addBenchmarkResult(@NotNull final BenchmarkResult benchmarkResult) {
        Objects.requireNonNull(benchmarkResult);

        benchmarkResults.add(benchmarkResult);
    }

    /**
     * @return {@code true} if this commit result has no benchmark results, otherwise {@code false}
     */
    boolean hasNoBenchmarkResults() {
        return benchmarkResults.isEmpty();
    }

    /**
     * Checks whether this commit result should be considered significant (if at least one property result is
     * significant). Sets this results significance accordingly.
     */
    void updateSignificance() {
        for (BenchmarkResult benchmarkResult : benchmarkResults) {
            for (BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                if (propertyResult.isSignificant()) {
                    significant = true;
                    return;
                }
            }
        }
        significant = false;
    }

    /**
     * @param errorMessage the error message if there was an error with this result. May be null if there was no error.
     */
    public void setErrorMessage(@Nullable final String errorMessage) {
        if (errorMessage != null && errorMessage.length() > MAX_STRING_LENGTH) {
            this.errorMessage = errorMessage.substring(0, MAX_STRING_LENGTH);
        } else {
            this.errorMessage = errorMessage;
        }
    }
}
