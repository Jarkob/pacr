package pacr.benchmarker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import pacr.benchmarker.services.*;
import pacr.benchmarker.services.git.GitHandler;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobExecutorTest {

    @Mock
    private GitHandler gitHandler;
    @Mock
    private JobDispatcher jobDispatcher;
    @Mock
    private BenchmarkingResult result;
    @Mock
    private IJobResultSender resultSender;
    private static final String relativePathToWorkingDir = "";
    private static final String REPOSITORY_URL = "url";
    private static final String COMMIT_HASH = "hash";
    private static final String PATH = "path";

    private JobExecutor jobExecutor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobExecutor = new JobExecutor(gitHandler, jobDispatcher, relativePathToWorkingDir);
        jobExecutor.setResultSender(resultSender);
    }

    @Test
    public void executeJob() {
        when(gitHandler.setupRepositoryForBenchmark(REPOSITORY_URL, COMMIT_HASH)).thenReturn(PATH);
        when(jobDispatcher.dispatchJob(PATH)).thenReturn(result);

        jobExecutor.executeJob(REPOSITORY_URL, COMMIT_HASH);
        verify(gitHandler).setupRepositoryForBenchmark(REPOSITORY_URL, COMMIT_HASH);

        ArgumentCaptor<JobResult> resultArgumentCaptor = ArgumentCaptor.forClass(JobResult.class);

        verify(resultSender).sendJobResults(resultArgumentCaptor.capture());

        JobResult jobResult = resultArgumentCaptor.getValue();
        assertEquals(result, jobResult.getBenchmarkingResult());
        assertEquals(COMMIT_HASH, jobResult.getCommitHash());
        assertEquals(REPOSITORY_URL, jobResult.getRepository());
    }

}
