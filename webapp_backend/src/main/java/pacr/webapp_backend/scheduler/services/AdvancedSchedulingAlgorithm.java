package pacr.webapp_backend.scheduler.services;

import java.util.Comparator;

/**
 * Implements a scheduling policy which makes sure that no job group
 * gets more benchmarking time than another.
 */
class AdvancedSchedulingAlgorithm implements Comparator<Job> {

    @Override
    public int compare(final Job job1, final Job job2) {
        if (job1 == null) {
            return 1;
        }
        if (job2 == null) {
            return -1;
        }

        final int timeSheetCompare = Long.compare(job1.getGroupTimeSheet(), job2.getGroupTimeSheet());

        if (timeSheetCompare == 0) {
            return job2.getQueued().compareTo(job1.getQueued());
        }

        return timeSheetCompare;
    }

}
