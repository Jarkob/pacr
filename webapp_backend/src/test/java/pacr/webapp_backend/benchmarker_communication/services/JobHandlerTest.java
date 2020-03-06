package pacr.webapp_backend.benchmarker_communication.services;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.JobDB;
import pacr.webapp_backend.database.JobGroupDB;
import pacr.webapp_backend.scheduler.services.Job;
import pacr.webapp_backend.scheduler.services.Scheduler;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IResultSaver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobHandlerTest extends SpringBootTestWithoutShell {

    private final String ADDRESS = "benchmarkerAddress";
    private final String JOB_GROUP = "jobGroup";
    private final String JOB_ID = "jobID";

    private JobHandler jobHandler;

    @Mock
    private IJob job;

    @Mock
    private IJobSender jobSender;

    @Mock
    private IBenchmarkerPool benchmarkerPool;

    @Mock
    private IResultSaver resultSaver;

    private Scheduler jobProvider;

    private final JobDB jobAccess;

    private final JobGroupDB jobGroupAccess;

    @Autowired
    public JobHandlerTest(final JobDB jobAccess, final JobGroupDB jobGroupAccess) {
        this.jobAccess = jobAccess;
        this.jobGroupAccess = jobGroupAccess;
    }

    @AfterEach
    public void cleanUp() {
        jobAccess.deleteAll();
        jobGroupAccess.deleteAll();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(job.getJobGroupTitle()).thenReturn(JOB_GROUP);
        when(job.getJobID()).thenReturn(JOB_ID);

        jobProvider = spy(new Scheduler(jobAccess, jobGroupAccess));

        this.jobHandler = spy(new JobHandler(jobSender, benchmarkerPool, jobProvider, resultSaver));
    }

    @Test
    void update_callsPopJob_benchmarkersAvailable() {
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);

        jobHandler.update();

        verify(jobHandler).executeJob();
    }

    @Test
    void update_doesntCallsPopJob_benchmarkersNotAvailable() {
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(false);

        jobHandler.update();

        verify(jobProvider, never()).popJob();
    }

    @Test
    void newRegistration_callsExecuteJob() {
        this.jobHandler.newRegistration();

        verify(jobHandler).executeJob();
    }

    @Test
    void executeJob_noError() {
        addJob(JOB_GROUP, JOB_ID);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);

        jobHandler.executeJob();

        verify(jobSender).sendJob(argThat(new BenchmarkerJobMatcher(job, ADDRESS)));
        verify(benchmarkerPool).occupyBenchmarker(ADDRESS);
    }

    @Test
    void executeJob_sendFailed() {
        addJob(JOB_GROUP, JOB_ID);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(false);

        jobHandler.executeJob();

        verify(jobSender).sendJob(argThat(new BenchmarkerJobMatcher(job, ADDRESS)));
        verify(benchmarkerPool, never()).occupyBenchmarker(ADDRESS);

        final ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobProvider).returnJob(jobCaptor.capture());

        final Job returnedJob = jobCaptor.getValue();
        assertEquals(JOB_GROUP, returnedJob.getJobGroupTitle());
        assertEquals(JOB_ID, returnedJob.getJobID());
    }

    @Test
    void executeJob_noFreeBenchmarker() {
        addJob(JOB_GROUP, JOB_ID);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(false);

        jobHandler.executeJob();

        verify(benchmarkerPool, never()).getFreeBenchmarker();
        verify(jobSender, never()).sendJob(any(BenchmarkerJob.class));
        verify(benchmarkerPool, never()).occupyBenchmarker(ADDRESS);

        final ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobProvider).returnJob(jobCaptor.capture());

        final Job returnedJob = jobCaptor.getValue();
        assertEquals(JOB_GROUP, returnedJob.getJobGroupTitle());
        assertEquals(JOB_ID, returnedJob.getJobID());
    }

    @Test
    void executeJob_noJob() {
        jobHandler.executeJob();

        verify(benchmarkerPool, never()).hasFreeBenchmarkers();
        verify(benchmarkerPool, never()).getFreeBenchmarker();
        verify(jobSender, never()).sendJob(any(BenchmarkerJob.class));
        verify(benchmarkerPool, never()).occupyBenchmarker(ADDRESS);
        verify(jobProvider, never()).returnJob(any());
    }

    @Test
    void executeJob_sendingDifficulties_resolved() {
        addJob(JOB_GROUP, JOB_ID);

        final BenchmarkerPool benchmarkerPool = new BenchmarkerPool();
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        this.jobHandler = new JobHandler(jobSender, benchmarkerPool, jobProvider, resultSaver);

        jobProvider.subscribe(jobHandler);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenAnswer(new Answer() {
            private int count = 100;

            public Object answer(final InvocationOnMock invocation) {
                count--;

                return count <= 0;
            }
        });

        jobHandler.executeJob();

        verify(jobSender, times(100)).sendJob(any(BenchmarkerJob.class));
        assertFalse(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void executeJob_sendingDifficulties_notResolved() {
        Scheduler scheduler = new Scheduler(jobAccess, jobGroupAccess);
        scheduler.addJobs(JOB_GROUP, List.of(JOB_ID));

        final BenchmarkerPool benchmarkerPool = new BenchmarkerPool();
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        this.jobHandler = new JobHandler(jobSender, benchmarkerPool, scheduler, resultSaver);

        scheduler.subscribe(jobHandler);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(false);

        jobHandler.executeJob();

        verify(jobSender, times(100)).sendJob(any(BenchmarkerJob.class));
        assertTrue(benchmarkerPool.hasFreeBenchmarkers());
    }

    @Test
    void executeJob_sendingDifficulties_multipleBenchmarkers() {
        final String ADDRESS_2 = ADDRESS + "Second";

        addJob(JOB_GROUP, JOB_ID);

        final BenchmarkerPool benchmarkerPool = new BenchmarkerPool();
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());
        benchmarkerPool.registerBenchmarker(ADDRESS_2, new SystemEnvironment());

        this.jobHandler = new JobHandler(jobSender, benchmarkerPool, jobProvider, resultSaver);

        jobProvider.subscribe(jobHandler);
        when(jobSender.sendJob(argThat(new BenchmarkerJobMatcher(ADDRESS)))).thenReturn(false);
        when(jobSender.sendJob(argThat(new BenchmarkerJobMatcher(ADDRESS_2)))).thenReturn(true);

        jobHandler.executeJob();

        final String remainingBenchmarker = benchmarkerPool.getFreeBenchmarker();

        verify(jobSender, times(2)).sendJob(any(BenchmarkerJob.class));
        assertEquals(ADDRESS, remainingBenchmarker);
    }

    @Test
    void receiveBenchmarkingResults_noError() {
        final JobResult result = new JobResult();

        addJob(JOB_GROUP, JOB_ID);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);
        jobHandler.executeJob();

        jobHandler.receiveBenchmarkingResults(ADDRESS, result);

        verify(benchmarkerPool).freeBenchmarker(ADDRESS);
        verify(resultSaver).saveBenchmarkingResults(result);
        verify(jobProvider, times(2)).popJob();
    }

    @Test
    void receiveBenchmarkingResults_unknownBenchmarker() {
        assertThrows(IllegalArgumentException.class, () -> {
            jobHandler.receiveBenchmarkingResults(ADDRESS, new JobResult());
        });
    }

    @Test
    void receiveBenchmarkingResults_noResult() {
        addJob(JOB_GROUP, JOB_ID);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);
        jobHandler.executeJob();

        jobHandler.receiveBenchmarkingResults(ADDRESS, null);

        verify(benchmarkerPool).freeBenchmarker(ADDRESS);
        verify(resultSaver, never()).saveBenchmarkingResults(any(JobResult.class));

        final ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobProvider).returnJob(jobCaptor.capture());

        final Job returnedJob = jobCaptor.getValue();
        assertEquals(JOB_GROUP, returnedJob.getJobGroupTitle());
        assertEquals(JOB_ID, returnedJob.getJobID());
    }

    @Test
    void connectionLostFor_noError() {
        addJob(JOB_GROUP, JOB_ID);

        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);
        jobHandler.executeJob();

        jobHandler.connectionLostFor(ADDRESS);

        final ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobProvider).returnJob(jobCaptor.capture());

        final Job returnedJob = jobCaptor.getValue();
        assertEquals(JOB_GROUP, returnedJob.getJobGroupTitle());
        assertEquals(JOB_ID, returnedJob.getJobID());
    }

    @Test
    void connectionLostFor_unknownBenchmarker() {
        jobHandler.connectionLostFor(ADDRESS);

        verify(jobProvider, never()).returnJob(any(IJob.class));
    }

    @Test
    void connectionLostFor_null() {
        jobHandler.connectionLostFor(null);

        verify(jobProvider, never()).returnJob(any(IJob.class));
    }

    @Test
    void connectionLostFor_empty() {
        jobHandler.connectionLostFor("");

        jobHandler.connectionLostFor(" ");

        verify(jobProvider, never()).returnJob(any(IJob.class));
    }

    @Test
    void getCurrentBenchmarkerJob_noError() {
        addJob(JOB_GROUP, JOB_ID);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);

        jobHandler.executeJob();

        final IJob currentJob = jobHandler.getCurrentBenchmarkerJob(ADDRESS);
        assertEquals(JOB_GROUP, currentJob.getJobGroupTitle());
        assertEquals(JOB_ID, currentJob.getJobID());
    }

    @Test
    void getCurrentBenchmarkerJob_noJobDispatched() {
        final IJob currentJob = jobHandler.getCurrentBenchmarkerJob(ADDRESS);

        assertNull(currentJob);
    }

    @Test
    void getCurrentBenchmarkerJob_invalidAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
           jobHandler.getCurrentBenchmarkerJob("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            jobHandler.getCurrentBenchmarkerJob(" ");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            jobHandler.getCurrentBenchmarkerJob(null);
        });
    }

    private void addJob(String groupTitle, String jobID) {
        jobProvider.addJobs(groupTitle, List.of(jobID));
    }

    private static class BenchmarkerJobMatcher implements ArgumentMatcher<BenchmarkerJob> {

        private final IJob job;
        private final String address;

        public BenchmarkerJobMatcher(final IJob job, final String address) {
            this.job = job;
            this.address = address;
        }

        public BenchmarkerJobMatcher(final String address) {
            this.address = address;
            this.job = null;
        }

        @Override
        public boolean matches(final BenchmarkerJob benchmarkerJob) {
            if (benchmarkerJob == null) {
                return false;
            }

            if (job == null) {
                return benchmarkerJob.getAddress().equals(address);
            }
            return benchmarkerJob.getAddress().equals(address)
                    && benchmarkerJob.getCommitHash().equals(job.getJobID())
                    && benchmarkerJob.getRepository().equals(job.getJobGroupTitle());
        }
    }
}
