package pacr.webapp_backend.benchmarker_communication.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import pacr.webapp_backend.scheduler.services.Scheduler;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IResultSaver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobHandlerTest {

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

    @Mock
    private Scheduler jobProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(job.getJobGroupTitle()).thenReturn(JOB_GROUP);
        when(job.getJobID()).thenReturn(JOB_ID);

        this.jobHandler = new JobHandler(jobSender, benchmarkerPool, jobProvider, resultSaver);
    }

    @Test
    void update_callsPopJob() {
        jobHandler.update();

        verify(jobProvider).popJob();
    }

    @Test
    void newRegistration_callsPopJob() {
        jobHandler.newRegistration();

        verify(jobProvider).popJob();
    }

    @Test
    void executeJob_noError() {
        when(jobProvider.popJob()).thenReturn(job);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);

        jobHandler.executeJob();

        verify(jobSender).sendJob(argThat(new BenchmarkerJobMatcher(job, ADDRESS)));
        verify(benchmarkerPool).occupyBenchmarker(ADDRESS);
    }

    @Test
    void executeJob_sendFailed() {
        when(jobProvider.popJob()).thenReturn(job);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(false);

        jobHandler.executeJob();

        verify(jobSender).sendJob(argThat(new BenchmarkerJobMatcher(job, ADDRESS)));
        verify(benchmarkerPool, never()).occupyBenchmarker(ADDRESS);
        verify(jobProvider).returnJob(job);
    }

    @Test
    void executeJob_noFreeBenchmarker() {
        when(jobProvider.popJob()).thenReturn(job);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(false);

        jobHandler.executeJob();

        verify(benchmarkerPool, never()).getFreeBenchmarker();
        verify(jobSender, never()).sendJob(any(BenchmarkerJob.class));
        verify(benchmarkerPool, never()).occupyBenchmarker(ADDRESS);
        verify(jobProvider).returnJob(job);
    }

    @Test
    void executeJob_noJob() {
        when(jobProvider.popJob()).thenReturn(null);

        jobHandler.executeJob();

        verify(benchmarkerPool, never()).hasFreeBenchmarkers();
        verify(benchmarkerPool, never()).getFreeBenchmarker();
        verify(jobSender, never()).sendJob(any(BenchmarkerJob.class));
        verify(benchmarkerPool, never()).occupyBenchmarker(ADDRESS);
        verify(jobProvider, never()).returnJob(any());
    }

    @Test
    void executeJob_sendingDifficulties_resolved() {
        Scheduler scheduler = new Scheduler();
        scheduler.addJob(JOB_GROUP, JOB_ID);

        BenchmarkerPool benchmarkerPool = new BenchmarkerPool();
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());

        this.jobHandler = new JobHandler(jobSender, benchmarkerPool, scheduler, resultSaver);

        scheduler.subscribe(jobHandler);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenAnswer(new Answer() {
            private int count = 100;

            public Object answer(InvocationOnMock invocation) {
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
        Scheduler scheduler = new Scheduler();
        scheduler.addJob(JOB_GROUP, JOB_ID);

        BenchmarkerPool benchmarkerPool = new BenchmarkerPool();
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

        Scheduler scheduler = new Scheduler();
        scheduler.addJob(JOB_GROUP, JOB_ID);

        BenchmarkerPool benchmarkerPool = new BenchmarkerPool();
        benchmarkerPool.registerBenchmarker(ADDRESS, new SystemEnvironment());
        benchmarkerPool.registerBenchmarker(ADDRESS_2, new SystemEnvironment());

        this.jobHandler = new JobHandler(jobSender, benchmarkerPool, scheduler, resultSaver);

        scheduler.subscribe(jobHandler);
        when(jobSender.sendJob(argThat(new BenchmarkerJobMatcher(ADDRESS)))).thenReturn(false);
        when(jobSender.sendJob(argThat(new BenchmarkerJobMatcher(ADDRESS_2)))).thenReturn(true);

        jobHandler.executeJob();

        String remainingBenchmarker = benchmarkerPool.getFreeBenchmarker();

        verify(jobSender, times(2)).sendJob(any(BenchmarkerJob.class));
        assertEquals(ADDRESS, remainingBenchmarker);
    }

    @Test
    void receiveBenchmarkingResults_noError() {
        JobResult result = new JobResult();

        when(jobProvider.popJob()).thenReturn(job);
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
        when(jobProvider.popJob()).thenReturn(job);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);
        jobHandler.executeJob();

        jobHandler.receiveBenchmarkingResults(ADDRESS, null);

        verify(benchmarkerPool).freeBenchmarker(ADDRESS);
        verify(resultSaver, never()).saveBenchmarkingResults(any(JobResult.class));
        verify(jobProvider).returnJob(job);
    }

    @Test
    void connectionLostFor_noError() {
        when(jobProvider.popJob()).thenReturn(job);
        when(benchmarkerPool.hasFreeBenchmarkers()).thenReturn(true);
        when(benchmarkerPool.getFreeBenchmarker()).thenReturn(ADDRESS);
        when(jobSender.sendJob(any(BenchmarkerJob.class))).thenReturn(true);
        jobHandler.executeJob();

        jobHandler.connectionLostFor(ADDRESS);

        verify(jobProvider).returnJob(job);
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

    private class BenchmarkerJobMatcher implements ArgumentMatcher<BenchmarkerJob> {

        private IJob job;
        private String address;

        public BenchmarkerJobMatcher(IJob job, String address) {
            this.job = job;
            this.address = address;
        }

        public BenchmarkerJobMatcher(String address) {
            this.address = address;
            this.job = null;
        }

        @Override
        public boolean matches(BenchmarkerJob benchmarkerJob) {
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
