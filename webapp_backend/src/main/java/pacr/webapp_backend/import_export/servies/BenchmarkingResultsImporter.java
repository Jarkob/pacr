package pacr.webapp_backend.import_export.servies;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.IRepositoryImporter;
import pacr.webapp_backend.shared.IResultImporter;

/**
 * Imports benchmarking results into the system.
 */
@Component
public class BenchmarkingResultsImporter {

    private IResultImporter resultImporter;
    private IRepositoryImporter repositoryImporter;

    /**
     * Creates a new BenchmarkingResultsImporter.
     *
     * @param resultImporter the resultImporter used to import the benchmarking results.
     * @param repositoryImporter the repositoryImporter used to add new repositories.
     */
    public BenchmarkingResultsImporter(final IResultImporter resultImporter, final IRepositoryImporter repositoryImporter) {
        Objects.requireNonNull(resultImporter, "The resultImporter cannot be null.");
        Objects.requireNonNull(repositoryImporter, "The repositoryImporter cannot be null.");

        this.resultImporter = resultImporter;
        this.repositoryImporter = repositoryImporter;
    }

    /**
     * Imports the given benchmarking results into the system.
     *
     * @param results the results to be imported.
     */
    public void importBenchmarkingResults(final Collection<OutputBenchmarkingResult> results) {
        final LocalDate now = LocalDate.now();

        final Collection<IBenchmarkingResult> benchmarkingResults = new ArrayList<>();

        for (final OutputBenchmarkingResult result : results) {
            repositoryImporter.importRepository(result.getRepositoryPullUrl(), now, result.getRepositoryName(),
                    result.getTrackedBranches());

            benchmarkingResults.addAll(result.getBenchmarkingResults());
        }

        resultImporter.importBenchmarkingResults(benchmarkingResults);
    }

}
