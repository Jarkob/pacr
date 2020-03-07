package pacr.webapp_backend.benchmarker_communication.endpoints;

import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pacr.webapp_backend.benchmarker_communication.services.BenchmarkerJob;
import pacr.webapp_backend.benchmarker_communication.services.JobHandler;
import pacr.webapp_backend.benchmarker_communication.services.JobResult;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BenchmarkingJobsControllerTest {

    private static final String ADDRESS = "benchmarkerAddress";
    private static final String COMMIT_HASH = "commitHash";
    private static final String REPOSITORY = "repository";

    private BenchmarkingJobsController jobsController;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private JobHandler jobHandler;

    @Mock
    private JobResult jobResult;

    @Mock
    private Principal principal;

    @Mock
    private BenchmarkerJob benchmarkerJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(principal.getName()).thenReturn(ADDRESS);

        when(benchmarkerJob.getAddress()).thenReturn(ADDRESS);
        when(benchmarkerJob.getCommitHash()).thenReturn(COMMIT_HASH);
        when(benchmarkerJob.getRepository()).thenReturn(REPOSITORY);

        this.jobsController = new BenchmarkingJobsController(template);
        this.jobsController.setJobHandler(jobHandler);
    }

    @Test
    void receiveBenchmarkingResults_noError() {
        boolean result = jobsController.receiveBenchmarkingResults(jobResult, principal);

        verify(jobHandler).receiveBenchmarkingResults(ADDRESS, jobResult);
        assertTrue(result);
    }

    @Test
    void receiveBenchmarkingResults_nullPrincipal() {
        boolean result = jobsController.receiveBenchmarkingResults(jobResult, null);

        verify(jobHandler, never()).receiveBenchmarkingResults(any(), any());
        assertFalse(result);
    }

    @Test
    void receiveBenchmarkingResults_nullJobResult() {
        boolean result = jobsController.receiveBenchmarkingResults(null, principal);

        verify(jobHandler, never()).receiveBenchmarkingResults(any(), any());
        assertFalse(result);
    }

    @Test
    void receiveBenchmarkingResults_nullAddress() {
        when(principal.getName()).thenReturn(null);

        boolean result = jobsController.receiveBenchmarkingResults(jobResult, principal);

        verify(jobHandler, never()).receiveBenchmarkingResults(any(), any());
        assertFalse(result);
    }

    @Test
    void receiveBenchmarkingResults_emptyAddress() {
        when(principal.getName()).thenReturn("");

        boolean result = jobsController.receiveBenchmarkingResults(jobResult, principal);

        verify(jobHandler, never()).receiveBenchmarkingResults(any(), any());
        assertFalse(result);
    }

    @Test
    void receiveBenchmarkingResults_blankAddress() {
        when(principal.getName()).thenReturn(" ");

        boolean result = jobsController.receiveBenchmarkingResults(jobResult, principal);

        verify(jobHandler, never()).receiveBenchmarkingResults(any(), any());
        assertFalse(result);
    }

    @Test
    void JobMessage_noArgs() {
        assertDoesNotThrow(() -> {
            JobMessage jobMessage = new JobMessage();
        });
    }

    @Test
    void sendJob_noError() {
        boolean result = jobsController.sendJob(benchmarkerJob);

        ArgumentCaptor<JobMessage> jobMessageCaptor = ArgumentCaptor.forClass(JobMessage.class);

        verify(template).convertAndSendToUser(eq(ADDRESS), eq("/queue/newJob"), jobMessageCaptor.capture());

        JobMessage jobMessage = jobMessageCaptor.getValue();
        assertNotNull(jobMessage);
        assertEquals(COMMIT_HASH, jobMessage.getCommitHash());
        assertEquals(REPOSITORY, jobMessage.getRepository());

        assertTrue(result);
    }

    @Test
    void sendJob_nullBenchmarkerJob() {
        boolean result = jobsController.sendJob(null);

        verify(template, never()).convertAndSendToUser(any(), any(), any());

        assertFalse(result);
    }

    @Test
    void sendJob_invalidAddressBenchmarkerJob() {
        // address is null
        when(benchmarkerJob.getAddress()).thenReturn(null);

        boolean result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);

        // address is empty
        when(benchmarkerJob.getAddress()).thenReturn("");

        result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);

        // address is blank
        when(benchmarkerJob.getAddress()).thenReturn(" ");

        result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);
    }

    @Test
    void sendJob_invalidRepositoryBenchmarkerJob() {
        // repository is null
        when(benchmarkerJob.getRepository()).thenReturn(null);

        boolean result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);

        // repository is empty
        when(benchmarkerJob.getRepository()).thenReturn("");

        result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);

        // repository is blank
        when(benchmarkerJob.getRepository()).thenReturn(" ");

        result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);
    }

    @Test
    void sendJob_invalidCommitHashBenchmarkerJob() {
        // commitHash is null
        when(benchmarkerJob.getCommitHash()).thenReturn(null);

        boolean result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);

        // commitHash is empty
        when(benchmarkerJob.getCommitHash()).thenReturn("");

        result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);

        // commitHash is blank
        when(benchmarkerJob.getCommitHash()).thenReturn(" ");

        result = jobsController.sendJob(benchmarkerJob);

        verify(template, never()).convertAndSendToUser(any(), any(), any());
        assertFalse(result);
    }
}
