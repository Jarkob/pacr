package pacr.webapp_backend.import_export.servies;

import java.util.Collection;

/**
 * Represents all imported benchmarking results for a repository.
 */
public class ImportedBenchmarkingResult {

    private Collection<BenchmarkingResult> benchmarkingResults;
    private String repositoryPullUrl;
    private String repositoryName;

    /**
     * Creates an empty ImportedBenchmarkingResult.
     *
     * Needed for Spring to work.
     */
    public ImportedBenchmarkingResult() {
    }

    /**
     * @return the imported benchmarking results.
     */
    public Collection<BenchmarkingResult> getBenchmarkingResults() {
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
