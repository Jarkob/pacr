package pacr.webapp_backend.import_export.servies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmarkingResult;

/**
 * Represents all benchmarking results for a repository to be exported or imported.
 */
@NoArgsConstructor
public class OutputBenchmarkingResult {

    @Getter
    private String repositoryPullUrl;

    @Getter
    private String repositoryName;

    @Getter
    private Set<String> trackedBranches;
    
    private Collection<BenchmarkingResult> benchmarkingResults;

    /**
     * Creates a new OutputBenchmarkingResult.
     *
     * @param benchmarkingResults the results belonging to the repository.
     * @param repositoryPullUrl the pull-url of the repository.
     * @param repositoryName the display name of the repository.
     * @param trackedBranches the tracked branches of the repository.
     */
    public OutputBenchmarkingResult(Collection<IBenchmarkingResult> benchmarkingResults,
                                        String repositoryPullUrl, String repositoryName, Set<String> trackedBranches) {

        Objects.requireNonNull(benchmarkingResults, "The benchmarkingResults cannot be null.");

        if (!StringUtils.hasText(repositoryPullUrl)) {
            throw new IllegalArgumentException("The repositoryPullUrl cannot be null.");
        }

        if (!StringUtils.hasText(repositoryName)) {
            throw new IllegalArgumentException("The repositoryName cannot be null.");
        }

        this.benchmarkingResults = new ArrayList<>();
        for (IBenchmarkingResult result : benchmarkingResults) {
            this.benchmarkingResults.add(new BenchmarkingResult(result));
        }

        this.repositoryPullUrl = repositoryPullUrl;
        this.repositoryName = repositoryName;
        this.trackedBranches = trackedBranches;
    }

    /**
     * @return the imported benchmarking results.
     */
    public Collection<? extends IBenchmarkingResult> getBenchmarkingResults() {
        return benchmarkingResults;
    }

}
