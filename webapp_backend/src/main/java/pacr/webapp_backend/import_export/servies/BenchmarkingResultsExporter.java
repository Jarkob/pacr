package pacr.webapp_backend.import_export.servies;

import java.util.Collection;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.IResultExporter;

/**
 * Encapsulates the logic to gather all benchmarking results that are exported.
 */
public class BenchmarkingResultsExporter {

    private IResultExporter resultExporter;

    /**
     * Creates a new BenchmarkingResultsExporter.
     *
     * @param resultExporter the resultExporter used to get the results which are exported.
     */
    public BenchmarkingResultsExporter(IResultExporter resultExporter) {
        if (resultExporter == null) {
            throw new IllegalArgumentException("The resultExporter cannot be null.");
        }

        this.resultExporter = resultExporter;
    }

    /**
     * @return a list of all benchmarking results to be exported.
     */
    public Collection<IBenchmarkingResult> exportBenchmarkingResults() {
        return resultExporter.exportAllBenchmarkingResults();
    }

}
