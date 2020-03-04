package pacr.webapp_backend.import_export.endpoints;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsExporter;
import pacr.webapp_backend.import_export.servies.BenchmarkingResultsImporter;
import pacr.webapp_backend.import_export.servies.OutputBenchmarkingResult;
import pacr.webapp_backend.shared.IAuthenticator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImportExportControllerTest {

    private static final String JWT = "jwt";

    private ImportExportController importExportController;

    @Mock
    private IAuthenticator authenticator;

    @Mock
    private BenchmarkingResultsImporter resultsImporter;

    @Mock
    private BenchmarkingResultsExporter resultsExporter;

    @Mock
    private Collection<OutputBenchmarkingResult> exportedResults;

    @Mock
    private Collection<OutputBenchmarkingResult> importedResults;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(authenticator.authenticate(JWT)).thenReturn(true);
        when(resultsExporter.exportBenchmarkingResults()).thenReturn(exportedResults);

        this.importExportController = new ImportExportController(authenticator, resultsImporter, resultsExporter);
    }

    @Test
    void exportBenchmarkingResults_noError() {
        final Collection<OutputBenchmarkingResult> results = importExportController.exportBenchmarkingResults(JWT);

        verify(authenticator).authenticate(JWT);
        verify(resultsExporter).exportBenchmarkingResults();
        assertEquals(exportedResults, results);
    }

    @Test
    void exportBenchmarkingResults_wrongJWT() {
        when(authenticator.authenticate(JWT)).thenReturn(false);

        final HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
           importExportController.exportBenchmarkingResults(JWT);
        }).getStatus();

        verify(resultsExporter, never()).exportBenchmarkingResults();
        verify(authenticator).authenticate(JWT);
        assertEquals(HttpStatus.UNAUTHORIZED, status);
    }

    @Test
    void importBenchmarkingResults_noError() {
        importExportController.importBenchmarkingResults(importedResults, JWT);

        verify(authenticator).authenticate(JWT);
        verify(resultsImporter).importBenchmarkingResults(importedResults);
    }

    @Test
    void importBenchmarkingResults_wrongJWT() {
        when(authenticator.authenticate(JWT)).thenReturn(false);

        final HttpStatus status = assertThrows(ResponseStatusException.class, () -> {
            importExportController.importBenchmarkingResults(importedResults, JWT);
        }).getStatus();

        verify(resultsImporter, never()).importBenchmarkingResults(any());
        verify(authenticator).authenticate(JWT);
        assertEquals(HttpStatus.UNAUTHORIZED, status);
    }
}
