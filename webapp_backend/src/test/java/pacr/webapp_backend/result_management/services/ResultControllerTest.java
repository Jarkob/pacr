package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pacr.webapp_backend.result_management.endpoints.ResultController;
import pacr.webapp_backend.shared.IAuthenticator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
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
    public static final int PAGE_NUM = 0;
    public static final int PAGE_SIZE = 200;

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

    @Test
    void getBenchmarkingResultForCommit_inputIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> resultController.getBenchmarkingResultForCommit(null));
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
        List<CommitHistoryItem> getterOutput = new LinkedList<>();
        when(resultGetterMock.getNewestResults()).thenReturn(getterOutput);

        List<CommitHistoryItem> testOutput = resultController.getNewBenchmarkingResults();

        verify(resultGetterMock).getNewestResults();
        assertEquals(getterOutput, testOutput);
    }

    /**
     * Tests whether getResultPageForBranchAndBenchmark correctly calls the ResultGetter.
     */
    @Test
    void getResultPageForBranchAndBenchmark_shouldCallResultGetter() {
        Pageable pageRequestInput = PageRequest.of(PAGE_NUM, PAGE_SIZE);

        HashMap<String, DiagramOutputResult> getterOutput = new HashMap<>();
        when(resultGetterMock.getBenchmarkResultsSubset(BENCHMARK_ID, REPO_ID, BRANCH_NAME, PAGE_NUM, PAGE_SIZE))
                .thenReturn(getterOutput);

        Map<String, DiagramOutputResult> testOutput = resultController.getResultPageForBranchAndBenchmark(BENCHMARK_ID,
                REPO_ID, BRANCH_NAME, pageRequestInput);

        verify(resultGetterMock).getBenchmarkResultsSubset(BENCHMARK_ID, REPO_ID, BRANCH_NAME, PAGE_NUM, PAGE_SIZE);
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
        verify(resultManagerMock).deleteBenchmarkingResults(Arrays.asList(HASH));
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
        verify(resultManagerMock, never()).deleteBenchmarkingResults(anyCollection());
    }

    @Test
    void deleteResult_inputIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> resultController.deleteBenchmarkingResult(null, null));
    }

    @Test
    void getResultsForRepository_pageable_shouldCallResultGetter() {
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        resultController.getResultsForRepository(REPO_ID, pageRequest);

        verify(resultGetterMock).getFullRepositoryResults(REPO_ID, pageRequest);
    }
}
