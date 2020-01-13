package pacr.webapp_backend.scheduler.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;

/**
 * Implements a scheduling policy which makes sure that no job group
 * gets more benchmarking time than another.
 */
class AdvancedSchedulingAlgorithm implements Comparator<Job> {

    @Override
    public int compare(Job job1, Job job2) {
        if (job1 == null) {
            return 1;
        }
        if (job2 == null) {
            return -1;
        }

        LocalDateTime now = LocalDateTime.now();

        long job1Score = calcScore(job1, now);
        long job2Score = calcScore(job2, now);

        // a lower score means that the job is scheduled before the other one
        if (job1Score < job2Score) {
            return -1;
        } else if (job1Score == job2Score) {
            return job2.getQueued().compareTo(job1.getQueued());
        }

        return 1;
    }

    /**
     * Calculates a score for the job based on the time it was queued and the job group's benchmarking time.
     *
     * Score calculation:
     *
     * Take the seconds from the time it was scheduled until now.
     * The job group's benchmarking time is added as a penalty so jobs which belong to a job group with a lot of
     * benchmarking time have a worse score.
     *
     * A lower score is better. This way the most recent job is always preferred.
     *
     * @param job
     * @param now
     * @return
     */
    private long calcScore(Job job, LocalDateTime now) {
        long nowSeconds = now.toEpochSecond(ZoneOffset.UTC);
        long jobSeconds = job.getQueued().toEpochSecond(ZoneOffset.UTC);

        long deltaJobToNow = nowSeconds - jobSeconds;

        // weigh the group time sheet double
        deltaJobToNow += job.getGroupTimeSheet() * 2;

        return deltaJobToNow;
    }

}
