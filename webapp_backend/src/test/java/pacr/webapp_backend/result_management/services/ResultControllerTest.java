package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pacr.webapp_backend.result_management.endpoints.ResultController;
import pacr.webapp_backend.shared.IAuthenticator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResultControllerTest {
    public static final int REPO_ID = 1;
    public static final String BRANCH_NAME = "branch";
    public static final String HASH = "hash";
    public static final int BENCHMARK_ID = 1;
    public static final String TOKEN = "token";

    @Mock
    private IAuthenticator authenticatorMock;
    @Mock
    private ResultGetter resultGetterMock;
    @Mock
    private ResultManager resultManagerMock;

    private ResultController resultController;

    @BeforeEach
    void setUp() {
        authenticatorMock = Mockito.mock(IAuthenticator.class);
        resultGetterMock = Mockito.mock(ResultGetter.class);
        resultManagerMock = Mockito.mock(ResultManager.class);

        resultController = new ResultController(authenticatorMock, resultGetterMock, resultManagerMock);
    }

    /**
     * Tests whether getResultsFromRepository correctly calls the ResultGetter.
     */
    @Test
    void getResultsFromRepository_shouldCallResultGetter() {
        HashMap<String, DiagramOutputResult> getterOutput = new HashMap<>();
        when(resultGetterMock.getRepositoryResults(REPO_ID)).thenReturn(getterOutput);

        Map<String, DiagramOutputResult> testOutput = resultController.getBenchmarkingResultsFromRepository(REPO_ID);

        verify(resultGetterMock).getRepositoryResults(REPO_ID);
        assertEquals(getterOutput, testOutput);
    }

    /**
     * Tests whether getResultsFromBranch correctly calls the ResultGetter.
     */
    @Test
    void getResultsFromBranch_shouldCallResultGetter() {
        HashMap<String, DiagramOutputResult> getterOutput = new HashMap<>();
        when(resultGetterMock.getBranchResults(REPO_ID, BRANCH_NAME)).thenReturn(getterOutput);

        Map<String, DiagramOutputResult> testOutput = resultController.getBenchmarkingResultsFromBranch(REPO_ID,
                BRANCH_NAME);

        verify(resultGetterMock).getBranchResults(REPO_ID, BRANCH_NAME);
        assertEquals(getterOutput, testOutput);
    }

    /**
     * Tests whether getResultForCommit correctly calls the ResultGetter.
     */
    @Test
    void getResultForCommit_shouldCallResultGetter() {
        OutputBenchmarkingResult outputMock = Mockito.mock(OutputBenchmarkingResult.class);
        when(resultGetterMock.getCommitResult(HASH)).thenReturn(outputMock);

        OutputBenchmarkingResult testOutput = resultController.getBenchmarkingResultForCommit(HASH);

        verify(resultGetterMock).getCommitResult(HASH);
        assertEquals(outputMock, testOutput);
    }

    /**
     * Tests whether getResultsForBenchmark correctly calls the ResultGetter.
     */
    @Test
    void getResultForBenchmark_shouldCallResultGetter() {
        HashMap<String, DiagramOutputResult> getterOutput = new HashMap<>();
        when(resultGetterMock.getBenchmarkResults(BENCHMARK_ID)).thenReturn(getterOutput);

        Map<String, DiagramOutputResult> testOutput = resultController.getBenchmarkingResultsForBenchmark(BENCHMARK_ID);
        verify(resultGetterMock).getBenchmarkResults(BENCHMARK_ID);
        assertEquals(getterOutput, testOutput);
    }

    /**
     * Tests whether getResultsForRepoAndBenchmark correctly calls the ResultGetter.
     */
    @Test
    void getResultForRepoAndBenchmark_shouldCallResultGetter() {
        HashMap<String, DiagramOutputResult> getterOutput = new HashMap<>();
        when(resultGetterMock.getBenchmarkResults(REPO_ID, BENCHMARK_ID)).thenReturn(getterOutput);

        Map<String, DiagramOutputResult> testOutput = resultController.getResultsForRepositoryAndBenchmark(REPO_ID,
                BENCHMARK_ID);
        verify(resultGetterMock).getBenchmarkResults(REPO_ID, BENCHMARK_ID);
        assertEquals(getterOutput, testOutput);
    }

    /**
     * Tests whether getNewResults correctly calls the ResultGetter.
     */
    @Test
    void getNewResults_shouldCallResultGetter() {
        List<OutputBenchmarkingResult> getterOutput = new LinkedList<>();
        when(resultGetterMock.getNewestResults()).thenReturn(getterOutput);

        List<OutputBenchmarkingResult> testOutput = resultController.getNewBenchmarkingResults();

        verify(resultGetterMock).getNewestResults();
        assertEquals(getterOutput, testOutput);
    }

    /**
     * Tests whether deleteResult correctly calls resultGetter if authentication succeeds.
     */
    @Test
    void deleteResult_authenticationSucceeds_shouldCallResultManager() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(true);

        ResponseEntity<Object> response = resultController.deleteBenchmarkingResult(HASH, TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(resultManagerMock).deleteBenchmarkingResults(HASH);
    }

    /**
     * Tests whether deleteResult correctly does nothing if authentication fails.
     */
    @Test
    void deleteResult_authenticationFails_shouldNotCallResultManager() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(false);

        ResponseEntity<Object> response = resultController.deleteBenchmarkingResult(HASH, TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(resultManagerMock, never()).deleteBenchmarkingResults(anyString());


    }
}
