package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
import static pacr.webapp_backend.result_management.services.SimpleBenchmark.PROPERTY_NAME;

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
        PageRequest pageRequest = PageRequest.of(PAGE_NUM, PAGE_SIZE);

        Page<CommitHistoryItem> getterOutput = new PageImpl<>(new LinkedList<>());
        when(resultGetterMock.getNewestResults(pageRequest)).thenReturn(getterOutput);

        Page<CommitHistoryItem> testOutput = resultController.getNewBenchmarkingResults(pageRequest);

        verify(resultGetterMock).getNewestResults(pageRequest);
        assertEquals(getterOutput, testOutput);
    }

    /**
     * Tests whether getResultPageForBranchAndBenchmark correctly calls the ResultGetter.
     */
    @Test
    void getResultPageForBranchAndBenchmark_shouldCallResultGetter() {
        LocalDateTime startTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(1).truncatedTo(ChronoUnit.SECONDS);
        ZoneOffset currentOffset = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());

        HashMap<String, DiagramOutputResult> getterOutput = new HashMap<>();
        when(resultGetterMock.getBenchmarkResultsSubset(BENCHMARK_ID, REPO_ID, BRANCH_NAME, startTime, endTime))
                .thenReturn(getterOutput);

        Map<String, DiagramOutputResult> testOutput = resultController.getResultPageForBranchAndBenchmark(BENCHMARK_ID,
                REPO_ID, BRANCH_NAME, startTime.toEpochSecond(currentOffset), endTime.toEpochSecond(currentOffset));

        verify(resultGetterMock).getBenchmarkResultsSubset(BENCHMARK_ID, REPO_ID, BRANCH_NAME, startTime, endTime);
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

    @Test
    void getMeasurementsOfPropertyForCommit_shouldCallResultGetter() {
        when(resultGetterMock.getMeasurementsOfPropertyForCommit(HASH, BENCHMARK_ID, PROPERTY_NAME))
                .thenReturn(Arrays.asList(0d));

        resultController.getMeasurementsOfPropertyForCommit(HASH, BENCHMARK_ID, PROPERTY_NAME);

        verify(resultGetterMock).getMeasurementsOfPropertyForCommit(HASH, BENCHMARK_ID, PROPERTY_NAME);
    }

    @Test
    void getMeasurementsOfPropertyForCommit_inputIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> resultController
                .getMeasurementsOfPropertyForCommit(null, 0, null));
    }
}
