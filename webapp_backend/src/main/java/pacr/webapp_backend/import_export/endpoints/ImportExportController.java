package pacr.webapp_backend.import_export.endpoints;

import java.util.Collection;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsExporter;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsImporter;
import pacr.webapp_backend.import_export.servies.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;

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
    public ImportExportController(@NotNull final IAuthenticator authenticator,
                              @NotNull final BenchmarkingResultsImporter resultsImporter,
                              @NotNull final BenchmarkingResultsExporter resultsExporter) {

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
    @RequestMapping("/export-results")
    public Collection<OutputBenchmarkingResult> exportBenchmarkingResults(@RequestHeader(name = "jwt") final String token) {
        if (authenticator.authenticate(token)) {
            return resultsExporter.exportBenchmarkingResults();
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Imports the given benchmarking results into the system. This is a secure method.
     *
     * @param token a jwt token which is checked before executing the method.
     * @param results the results which are imported.
     */
    @PostMapping("/import-results")
    public void importBenchmarkingResults(@RequestBody final Collection<OutputBenchmarkingResult> results, @RequestHeader(name = "jwt") final String token) {
        if (authenticator.authenticate(token)) {
            resultsImporter.importBenchmarkingResults(results);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

}
