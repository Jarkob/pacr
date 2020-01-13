package pacr.webapp_backend.scheduler.services;

import java.util.Comparator;

/**
 * Implements a normal FIFO scheduling policy.
 */
class FIFOSchedulingAlgorithm implements Comparator<Job> {
    @Override
    public int compare(Job job1, Job job2) {
        if (job1 == null) {
            return 1;
        }
        if (job2 == null) {
            return -1;
        }

        return job1.getQueued().compareTo(job2.getQueued());
    }
}
