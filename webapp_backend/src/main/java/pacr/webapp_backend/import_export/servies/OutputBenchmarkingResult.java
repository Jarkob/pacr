package pacr.webapp_backend.import_export.servies;

import java.util.Collection;
import java.util.Objects;
import pacr.webapp_backend.shared.IBenchmarkingResult;

/**
 * Represents all benchmarking results for a repository to be exported or imported.
 */
public class OutputBenchmarkingResult {

    private Collection<IBenchmarkingResult> benchmarkingResults;
    private String repositoryPullUrl;
    private String repositoryName;

    /**
     * Creates an empty ImportedBenchmarkingResult.
     *
     * Needed for Spring to work.
     */
    public OutputBenchmarkingResult() {
    }

    /**
     * Creates a new OutputBenchmarkingResult.
     *
     * @param benchmarkingResults the results belonging to the repository.
     * @param repositoryPullUrl the pull-url of the repository.
     * @param repositoryName the display name of the repository.
     */
    public OutputBenchmarkingResult(Collection<IBenchmarkingResult> benchmarkingResults,
                                        String repositoryPullUrl, String repositoryName) {

        Objects.requireNonNull(benchmarkingResults, "The benchmarkingResults cannot be null.");

        if (!stringIsValid(repositoryPullUrl)) {
            throw new IllegalArgumentException("The repositoryPullUrl cannot be null.");
        }

        if (!stringIsValid(repositoryName)) {
            throw new IllegalArgumentException("The repositoryName cannot be null.");
        }

        this.benchmarkingResults = benchmarkingResults;
        this.repositoryPullUrl = repositoryPullUrl;
        this.repositoryName = repositoryName;
    }

    private boolean stringIsValid(String str) {
        return str != null && !str.isEmpty() && !str.isBlank();
    }

    /**
     * @return the imported benchmarking results.
     */
    public Collection<IBenchmarkingResult> getBenchmarkingResults() {
        return benchmarkingResults;
    }

    /**
     * @return the repository pull-url the benchmarking results belong to.
     */
    public String getRepositoryPullUrl() {
        return repositoryPullUrl;
    }

    /**
     * @return the display name of the repository.
     */
    public String getRepositoryName() {
        return repositoryName;
    }
}
