package pacr.webapp_backend.scheduler.services;

import java.util.Comparator;

/**
 * Implements a normal FIFO scheduling policy.
 */
class FIFOSchedulingAlgorithm implements Comparator<Job> {
    @Override
    public int compare(Job o1, Job o2) {
        return o1.getQueued().compareTo(o2.getQueued());
    }
}
