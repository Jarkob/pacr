package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.JobDB;
import pacr.webapp_backend.database.JobGroupDB;
import pacr.webapp_backend.shared.IJob;
import pacr.webapp_backend.shared.IObserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class SchedulerTest extends SpringBootTestWithoutShell {

    private Scheduler scheduler;

    final String JOB_GROUP = "jobGroup";
    final String JOB_ID = "jobID";

    private JobDB jobAccess;
    private JobGroupDB jobGroupAccess;

    @Autowired
    public SchedulerTest(JobDB jobAccess, JobGroupDB jobGroupAccess) {
        this.jobAccess = spy(jobAccess);
        this.jobGroupAccess = spy(jobGroupAccess);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        scheduler = spy(new Scheduler(jobAccess, jobGroupAccess));
    }

    @AfterEach
    public void cleanUp() {
        jobAccess.deleteAll();
        jobGroupAccess.deleteAll();
    }

    @Test
    void Scheduler_loadJobsOnConstruct() {
        JobGroup group1 = new JobGroup(JOB_GROUP + 1);
        JobGroup group2 = new JobGroup(JOB_GROUP + 2);

        final int amtJobsGroup1 = 5;
        final String jobsGroup1Suffix = "G1";
        saveJobsToDatabase(amtJobsGroup1, jobsGroup1Suffix, false, group1);

        final int amtJobsGroup2 = 5;
        final String jobsGroup2Suffix = "G2";
        saveJobsToDatabase(amtJobsGroup2, jobsGroup2Suffix, false, group2);

        final int amtPrioritizedGroup1 = 5;
        final String prioritizedGroup1Suffix = "GP1";
        saveJobsToDatabase(amtPrioritizedGroup1, prioritizedGroup1Suffix, true, group1);

        Scheduler scheduler = new Scheduler(jobAccess, jobGroupAccess);
        scheduler.loadJobsFromStorage();

        assertEquals(amtJobsGroup1 + amtJobsGroup2, scheduler.getJobsQueue().size());
        assertEquals(amtPrioritizedGroup1, scheduler.getPrioritizedQueue().size());

        int actualAmtJobsGroup1 = 0;
        int actualAmtJobsGroup2 = 0;
        for (Job job : scheduler.getJobsQueue()) {
            if (job.getJobGroupTitle().equals(group1.getTitle())) {
                actualAmtJobsGroup1++;
            } else if (job.getJobGroupTitle().equals(group2.getTitle())) {
                actualAmtJobsGroup2++;
            } else {
                fail("Unknown group was added.");
            }
        }

        int actualAmtPrioritizedGroup1 = 0;
        for (Job job : scheduler.getPrioritizedQueue()) {
            if (job.getJobGroupTitle().equals(group1.getTitle())) {
                actualAmtPrioritizedGroup1++;
            } else {
                fail("Unknown group was added.");
            }
        }

        assertEquals(amtJobsGroup1, actualAmtJobsGroup1);
        assertEquals(amtJobsGroup2, actualAmtJobsGroup2);
        assertEquals(amtPrioritizedGroup1, actualAmtPrioritizedGroup1);
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
    void addJob_invalidGroupTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
           scheduler.addJob(null, JOB_ID);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJob("", JOB_ID);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJob(" ", JOB_ID);
        });
    }

    @Test
    void addJob_invalidJobId() {
        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJob(JOB_GROUP,null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJob(JOB_GROUP,"");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJob(JOB_GROUP, " ");
        });
    }

    @Test
    void addJobs_noError() {
        Collection<String> jobIds = new ArrayList<>();

        final int amtJobs = 5;
        for (int i = 0; i < amtJobs; i++) {
            jobIds.add(JOB_ID + i);
        }

        scheduler.addJobs(JOB_GROUP, jobIds);

        Collection<Job> addedJobs = scheduler.getJobsQueue();

        assertEquals(amtJobs, addedJobs.size());
        for (Job job : addedJobs) {
            assertEquals(JOB_GROUP, job.getJobGroupTitle());
        }

        verify(scheduler).updateAll();
    }

    @Test
    void addJobs_invalidGroupTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJobs(null, new ArrayList<>());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJobs("", new ArrayList<>());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.addJobs(" ", new ArrayList<>());
        });

        verify(scheduler, never()).updateAll();
    }

    @Test
    void addJobs_invalidJobIds() {
        Collection<String> jobIds = new ArrayList<>();

        final int amtJobs = 5;
        for (int i = 0; i < amtJobs; i++) {
            jobIds.add(JOB_ID + i);
        }
        jobIds.add(null);
        jobIds.add("");
        jobIds.add(" ");

        scheduler.addJobs(JOB_GROUP, jobIds);

        Collection<Job> addedJobs = scheduler.getJobsQueue();

        assertEquals(amtJobs, addedJobs.size());
        for (Job job : addedJobs) {
            assertEquals(JOB_GROUP, job.getJobGroupTitle());
        }

        assertThrows(NullPointerException.class, () -> {
            scheduler.addJobs(JOB_GROUP, null);
        });
    }

    @Test
    void removeJobGroup_noError() throws InterruptedException {
        final int amtJobs = 3;
        final int amtPrioritized = 3;
        fillSchedulerWithJobs(amtJobs, amtPrioritized);

        // groupTitle is set by fillSchedulerWithJobs
        final String groupToRemove = JOB_GROUP + 0;

        scheduler.removeJobGroup(groupToRemove);

        Collection<Job> jobs = scheduler.getJobsQueue();
        Collection<Job> prioritized = scheduler.getPrioritizedQueue();

        // fillSchedulerWithJobs adds groups with one job associated
        // JOB_GROUP0 has a prioritized job
        assertEquals(amtPrioritized - 1, prioritized.size());
        assertEquals(amtJobs, jobs.size());

        ArgumentCaptor<JobGroup> groupCaptor = ArgumentCaptor.forClass(JobGroup.class);
        verify(jobGroupAccess).deleteGroup(groupCaptor.capture());

        JobGroup group = groupCaptor.getValue();
        assertEquals(groupToRemove, group.getTitle());
    }

    @Test
    void removeJobGroup_unknownGroup() {
        scheduler.removeJobGroup(JOB_GROUP);

        verify(jobAccess, never()).deleteJob(any());
        verify(jobGroupAccess, never()).deleteGroup(any());
    }

    @Test
    void removeJobGroup_invalidGroupTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
           scheduler.removeJobGroup(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.removeJobGroup("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.removeJobGroup(" ");
        });
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
    void givePriorityTo_invalidGroupTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.givePriorityTo(null, JOB_ID);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.givePriorityTo("", JOB_ID);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.givePriorityTo(" ", JOB_ID);
        });
    }

    @Test
    void givePriorityTo_invalidJobId() {
        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.givePriorityTo(JOB_GROUP,null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.givePriorityTo(JOB_GROUP, "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.givePriorityTo(JOB_GROUP, " ");
        });
    }

    @Test
    void popJob_noPrioritized_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        IJob job = scheduler.popJob();

        assertNotNull(job);
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
    void popJob_multipleCalls() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        IJob job = scheduler.popJob();

        assertNotNull(job);
        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());

        checkSchedulerQueue(0, 0);

        job = scheduler.popJob();

        assertNull(job);
        verify(scheduler).removeJobGroup(JOB_GROUP);
    }

    @Test
    void returnJob_noError() {
        scheduler.addJob(JOB_GROUP, JOB_ID);

        checkSchedulerQueue(1, 0);

        IJob job = scheduler.popJob();

        checkSchedulerQueue(0, 0);

        scheduler.returnJob(job);

        checkSchedulerQueue(1, 0);

        List<Job> jobs = scheduler.getJobsQueue();
        Job job1 = jobs.get(0);

        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());
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

        List<Job> jobs = scheduler.getJobsQueue();
        Job job1 = jobs.get(0);

        assertEquals(JOB_GROUP, job.getJobGroupTitle());
        assertEquals(JOB_ID, job.getJobID());
    }

    @Test
    void updateTimeSheet_noError() {
        final long TIME = 13;

        scheduler.addJob(JOB_GROUP, JOB_ID);

        Job job = getFirstJobInJobsQueue();

        assertEquals(0, job.getGroupTimeSheet());

        scheduler.addToGroupTimeSheet(JOB_GROUP, TIME);

        job = getFirstJobInJobsQueue();

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
            Thread.sleep(1000);
        }

        for (int i = amtPrioritizedJobs; i < amtJobs + amtPrioritizedJobs; i++) {
            scheduler.addJob(JOB_GROUP + i, JOB_ID + i);
            Thread.sleep(1000);
        }
    }

    private Job getFirstJobInJobsQueue() {
        List<Job> jobs = scheduler.getJobsQueue();

        assertTrue(jobs.size() > 0);

        return jobs.get(0);
    }

    private void saveJobsToDatabase(int amount, String suffix, boolean prioritized, JobGroup group) {
        jobGroupAccess.saveJobGroup(group);

        for (int i = 0; i < amount; i++) {
            Job job = new Job(JOB_ID + suffix + i, group);
            job.setPrioritized(prioritized);
            jobAccess.saveJob(job);
        }
    }

}
