package pacr.webapp_backend.import_export.servies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.IRepository;
import pacr.webapp_backend.shared.IResultExporter;

/**
 * Encapsulates the logic to gather all benchmarking results that are exported.
 */
@Component
public class BenchmarkingResultsExporter {

    private IExportRepositoryAccess repositoryAccess;
    private IResultExporter resultExporter;

    /**
     * Creates a new BenchmarkingResultsExporter.
     *
     * @param resultExporter the resultExporter used to get the results which are exported.
     * @param repositoryAccess the repositoryAccess used to fetch metadata from repositories.
     */
    public BenchmarkingResultsExporter(IResultExporter resultExporter, IExportRepositoryAccess repositoryAccess) {
        Objects.requireNonNull(resultExporter, "The resultExporter cannot be null.");
        Objects.requireNonNull(repositoryAccess, "The repositoryAccess cannot be null.");

        this.resultExporter = resultExporter;
        this.repositoryAccess = repositoryAccess;
    }

    /**
     * @return a list of all benchmarking results to be exported.
     */
    public Collection<OutputBenchmarkingResult> exportBenchmarkingResults() {
        Map<Integer, List<IBenchmarkingResult>> exportedResults = new HashMap<>();

        // sort results into buckets. Each bucket is for one repository which is identified by its id.
        for (IBenchmarkingResult result : resultExporter.exportAllBenchmarkingResults()) {
            final int repositoryID = result.getRepositoryID();

            if (!exportedResults.containsKey(repositoryID)) {
                exportedResults.put(repositoryID, new ArrayList<>());
            }

            exportedResults.get(repositoryID).add(result);
        }

        List<OutputBenchmarkingResult> outputResults = new ArrayList<>();

        for (int repositoryID : exportedResults.keySet()) {
            IRepository repository = repositoryAccess.findGitRepositoryById(repositoryID);

            if (repository != null) {
                OutputBenchmarkingResult outputResult = new OutputBenchmarkingResult(exportedResults.get(repositoryID),
                        repository.getPullURL(), repository.getName(), repository.getTrackedBranchNames());

                outputResults.add(outputResult);
            }
        }

        return outputResults;
    }

}
