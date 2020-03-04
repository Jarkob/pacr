package pacr.webapp_backend.scheduler.services;

import java.util.Comparator;

/**
 * Implements a scheduling policy which makes sure that no job group
 * gets more benchmarking time than another.
 */
class GroupSchedulingAlgorithm implements Comparator<JobGroup> {

    @Override
    public int compare(final JobGroup jobGroup1, final JobGroup jobGroup2) {
        if (jobGroup1 == null) {
            return 1;
        }
        if (jobGroup2 == null) {
            return -1;
        }

        return Long.compare(jobGroup1.getTimeSheet(), jobGroup2.getTimeSheet());
    }

}
