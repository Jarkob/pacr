package pacr.webapp_backend.import_export.endpoints;

import java.util.Collection;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsExporter;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsImporter;
import pacr.webapp_backend.import_export.servies.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.IAuthenticator;

/**
 * Handles import and export requests for benchmarking results.
 */
@RestController
public class ImportExportController {

    private IAuthenticator authenticator;

    private BenchmarkingResultsImporter resultsImporter;
    private BenchmarkingResultsExporter resultsExporter;

    /**
     * Creates a new ImportExportController.
     *
     * @param authenticator the authenticator which provides authentication services for secure methods.
     * @param resultsImporter the resultsImporter used to import benchmarking results.
     * @param resultsExporter the resultsExporter used to export benchmarking results.
     */
    public ImportExportController(@NotNull IAuthenticator authenticator,
                              @NotNull BenchmarkingResultsImporter resultsImporter,
                              @NotNull BenchmarkingResultsExporter resultsExporter) {

        Objects.requireNonNull(authenticator, "The authenticator cannot be null.");
        Objects.requireNonNull(resultsImporter, "The resultsImporter cannot be null.");
        Objects.requireNonNull(resultsExporter, "The resultsExporter cannot be null.");

        this.authenticator = authenticator;
        this.resultsImporter = resultsImporter;
        this.resultsExporter = resultsExporter;
    }

    /**
     * Exports all benchmarking results currently in the system. This is a secure method.
     *
     * @param token a jwt token which is checked before executing the method.
     * @return a list of benchmarking results or null if the token is invalid.
     */
    @RequestMapping("/exportResults")
    public Collection<OutputBenchmarkingResult> exportBenchmarkingResults(@RequestHeader(name = "jwt") String token) {
        if (authenticator.authenticate(token)) {
            return resultsExporter.exportBenchmarkingResults();
        }

        return null;
    }

    /**
     * Imports the given benchmarking results into the system. This is a secure method.
     *
     * @param token a jwt token which is checked before executing the method.
     * @param results the results which are imported.
     */
    @PostMapping("/importResults")
    public void exportBenchmarkingResults(@RequestBody Collection<OutputBenchmarkingResult> results, @RequestHeader(name = "jwt") String token) {
        if (authenticator.authenticate(token)) {
            resultsImporter.importBenchmarkingResults(results);
        }
    }

}
