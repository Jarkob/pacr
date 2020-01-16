package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IObserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SchedulerTest {

    private Scheduler scheduler;

    final String JOB_GROUP = "jobGroup";
    final String JOB_ID = "jobID";

    @BeforeEach
    void initialize() {
        scheduler = new Scheduler();
    }

    @Test
    void addJob_observersNotified() {
        IObserver observer = mock(IObserver.class);

        scheduler.subscribe(observer);

        scheduler.addJob(JOB_GROUP, JOB_ID);

        verify(observer).update();
    }

    @Test
    void addJob_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        checkSchedulerQueue(1, 0);

        List<Job> jobs = scheduler.getJobsQueue();
        Job job = jobs.get(0);

        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());
    }

    @Test
    void givePriorityTo_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        checkSchedulerQueue(1, 0);

        boolean result = scheduler.givePriorityTo(JOB_GROUP, JOB_ID);

        checkSchedulerQueue(0, 1);
        assertTrue(result);
    }

    @Test
    void givePriorityTo_jobNotFound() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        checkSchedulerQueue(1, 0);

        boolean result;

        result = scheduler.givePriorityTo(JOB_GROUP + "aWrongGroupTitle", JOB_ID);
        assertFalse(result);

        result = scheduler.givePriorityTo(JOB_GROUP, JOB_ID + "aWrongJobID");
        assertFalse(result);

        result = scheduler.givePriorityTo(JOB_GROUP + "aWrongGroupTitle", JOB_ID + "aWrongJobID");
        assertFalse(result);

        checkSchedulerQueue(1, 0);
    }

    @Test
    void popJob_noPrioritized_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        IJob job = scheduler.popJob();

        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());

        checkSchedulerQueue(0, 0);
    }

    @Test
    void popJob_withPrioritized_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);
        scheduler.givePriorityTo(JOB_GROUP, JOB_ID);

        IJob job = scheduler.popJob();

        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());

        checkSchedulerQueue(0, 0);
    }

    @Test
    void popJob_withManyJobs_noError() throws InterruptedException {
        final int amtJobs = 3;
        final int amtPrioritizedJobs = 4;

        // add a special job to the prioritized queue
        // this job should be the first to get popped
        scheduler.addJob(JOB_GROUP, JOB_ID);
        scheduler.givePriorityTo(JOB_GROUP, JOB_ID);

        fillSchedulerWithJobs(amtJobs, amtPrioritizedJobs);

        // + 1 accounts for the special job added
        checkSchedulerQueue(amtJobs, amtPrioritizedJobs + 1);

        IJob job = scheduler.popJob();

        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());

        checkSchedulerQueue(amtJobs, amtPrioritizedJobs);
    }

    @Test
    void popJob_changedPriority() throws InterruptedException {
        scheduler.addJob(JOB_GROUP, JOB_ID);
        Thread.sleep(1000);
        scheduler.addJob(JOB_GROUP + 1, JOB_ID + 1);

        // jobs belonging to JOB_GROUP1 should be scheduled last.
        final long time = 10;
        scheduler.addToGroupTimeSheet(JOB_GROUP + 1, time);

        IJob job = scheduler.popJob();

        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());
    }

    @Test
    void returnJob_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        checkSchedulerQueue(1, 0);

        IJob job = scheduler.popJob();

        checkSchedulerQueue(0, 0);

        scheduler.returnJob(job);

        checkSchedulerQueue(1, 0);
    }

    @Test
    void returnJob_beforePrioritized_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);
        scheduler.givePriorityTo(JOB_GROUP, JOB_ID);

        checkSchedulerQueue(0, 1);

        IJob job = scheduler.popJob();

        checkSchedulerQueue(0, 0);

        scheduler.returnJob(job);

        checkSchedulerQueue(1, 0);
    }

    @Test
    void updateTimeSheet_noError() {
        final long TIME = 13;

        scheduler.addJob(JOB_GROUP, JOB_ID);

        List<Job> jobs = scheduler.getJobsQueue();

        Job job = jobs.get(0);
        assertEquals(0, job.getGroupTimeSheet());

        scheduler.addToGroupTimeSheet(JOB_GROUP, TIME);
        assertEquals(TIME, job.getGroupTimeSheet());
    }

    @Test
    void updateTimeSheet_unknownGroup() {
        final long TIME = 13;

        scheduler.addJob(JOB_GROUP, JOB_ID);

        List<Job> jobs = scheduler.getJobsQueue();

        Job job = jobs.get(0);
        assertEquals(0, job.getGroupTimeSheet());

        scheduler.addToGroupTimeSheet(JOB_GROUP + "wrongGroupTitle", TIME);
        assertEquals(0, job.getGroupTimeSheet());
    }

    @Test
    void getJobsQueue_noError() throws InterruptedException {
        int amtJobs = 3;
        int amtPrioritizedJobs = 3;

        fillSchedulerWithJobs(amtJobs, amtPrioritizedJobs);

        List<Job> jobs = scheduler.getJobsQueue();

        assertEquals(amtJobs, jobs.size());

        for (int i = 0; i < amtJobs; i++) {
            // is set by fillSchedulerWithJobs
            int jobNumber = amtJobs + amtPrioritizedJobs - i - 1;

            Job job = jobs.get(i);
            assertEquals(JOB_GROUP + jobNumber, job.getJobGroupTitle());
            assertEquals(JOB_ID + jobNumber, job.getJobID());
        }
    }

    @Test
    void getJobsQueue_differentTimeSheets_noError() throws InterruptedException {
        final int TIME = 100;
        int amtJobs = 3;
        int amtPrioritizedJobs = 3;

        fillSchedulerWithJobs(amtJobs, amtPrioritizedJobs);

        final int jobWithDifferentTimeSheet = amtJobs + amtPrioritizedJobs - 1;
        scheduler.addToGroupTimeSheet(JOB_GROUP + jobWithDifferentTimeSheet, TIME);

        List<Job> jobs = scheduler.getJobsQueue();

        assertEquals(amtJobs, jobs.size());

        // treat the last one differently
        for (int i = 0; i < amtJobs - 1; i++) {
            // is set by fillSchedulerWithJobs
            // - 2 because the jobWithDifferentTimeSheet should be the last
            int jobNumber = amtJobs + amtPrioritizedJobs - i - 2;

            Job job = jobs.get(i);
            assertEquals(JOB_GROUP + jobNumber, job.getJobGroupTitle());
            assertEquals(JOB_ID + jobNumber, job.getJobID());
        }

        Job job = jobs.get(amtJobs - 1);
        assertEquals(JOB_GROUP + jobWithDifferentTimeSheet, job.getJobGroupTitle());
        assertEquals(JOB_ID + jobWithDifferentTimeSheet, job.getJobID());
    }

    @Test
    void getPrioritizedQueue_noError() throws InterruptedException {
        int amtJobs = 3;
        int amtPrioritizedJobs = 3;

        fillSchedulerWithJobs(amtJobs, amtPrioritizedJobs);

        List<Job> prioritized = scheduler.getPrioritizedQueue();

        assertEquals(amtPrioritizedJobs, prioritized.size());

        for (int i = 0; i < amtPrioritizedJobs; i++) {
            Job job = prioritized.get(i);
            assertEquals(JOB_GROUP + i, job.getJobGroupTitle());
            assertEquals(JOB_ID + i, job.getJobID());
        }
    }

    @Test
    void getPrioritizedQueue_differentTimeSheets_noError() throws InterruptedException {
        final int TIME = 100;
        int amtJobs = 3;
        int amtPrioritizedJobs = 3;

        fillSchedulerWithJobs(amtJobs, amtPrioritizedJobs);

        scheduler.addToGroupTimeSheet(JOB_GROUP + 0, TIME);

        List<Job> prioritized = scheduler.getPrioritizedQueue();

        assertEquals(amtPrioritizedJobs, prioritized.size());

        for (int i = 0; i < amtPrioritizedJobs; i++) {
            Job job = prioritized.get(i);
            assertEquals(JOB_GROUP + i, job.getJobGroupTitle());
            assertEquals(JOB_ID + i, job.getJobID());
        }
    }

    @Test
    void subscribe_noError() {
        IObserver observer = mock(IObserver.class);

        scheduler.subscribe(observer);

        scheduler.updateAll();

        verify(observer).update();
    }

    @Test
    void unsubscribe_noError() {
        IObserver observer = mock(IObserver.class);
        scheduler.subscribe(observer);

        scheduler.unsubscribe(observer);

        scheduler.updateAll();

        verify(observer, never()).update();
    }

    @Test
    void updateAll_noError() {
        List<IObserver> observers = new ArrayList<>();

        int amtObservers = 10;
        for (int i = 0; i < amtObservers; i++) {
            IObserver observer = mock(IObserver.class);
            observers.add(observer);
            scheduler.subscribe(observer);
        }

        scheduler.updateAll();

        for (IObserver observer : observers) {
            verify(observer).update();
        }
    }

    @Test
    void resetJobGroupTimeSheets_noError() {
        final String secondGroupTitle = JOB_GROUP + 1;
        final String secondJobID = JOB_ID + 1;

        scheduler.addJob(JOB_GROUP, JOB_ID);
        scheduler.addJob(secondGroupTitle, secondJobID);

        scheduler.givePriorityTo(secondGroupTitle, secondJobID);

        final long time = 20;
        scheduler.addToGroupTimeSheet(JOB_GROUP, time);
        scheduler.addToGroupTimeSheet(secondGroupTitle, time);

        scheduler.resetJobGroupTimeSheets();

        List<Job> jobs = scheduler.getJobsQueue();
        assertEquals(1, jobs.size());
        for (Job job : jobs) {
            assertEquals(0, job.getGroupTimeSheet());
        }

        List<Job> prioritized = scheduler.getPrioritizedQueue();
        assertEquals(1, prioritized.size());
        for (Job job : prioritized) {
            assertEquals(0, job.getGroupTimeSheet());
        }
    }

    private void checkSchedulerQueue(int amtJobs, int amtPrioritizedJobs) {
        List<Job> jobs = scheduler.getJobsQueue();
        List<Job> prioritized = scheduler.getPrioritizedQueue();

        assertEquals(amtJobs, jobs.size());
        assertEquals(amtPrioritizedJobs, prioritized.size());
    }

    private void fillSchedulerWithJobs(int amtJobs, int amtPrioritizedJobs) throws InterruptedException {
        for (int i = 0; i < amtPrioritizedJobs; i++) {
            scheduler.addJob(JOB_GROUP + i, JOB_ID + i);
            scheduler.givePriorityTo(JOB_GROUP + i, JOB_ID + i);
            Thread.sleep(1);
        }

        for (int i = amtPrioritizedJobs; i < amtJobs + amtPrioritizedJobs; i++) {
            scheduler.addJob(JOB_GROUP + i, JOB_ID + i);
            Thread.sleep(1);
        }
    }

}
