package pacr.webapp_backend.dashboard_management;

import java.util.List;

/**
 * Instances of this class represent line diagram modules on a dashboard.
 * One of these leaderboard modules shows the line diagram for specific benchmarks and repositories.
 *
 * @author Benedikt Hahn
 */
public class LineDiagramDashboardModule extends DashboardModule {
    List<String> trackedRepositories;

    List<String> trackedBenchmarks;

    /**
     * Sets the tracked repositories to the given list of repository names.
     * @param trackedRepositories The new tracked repositories.
     */
    void setTrackedRepositories(List<String> trackedRepositories) {
        this.trackedRepositories = trackedRepositories;
    }

    /**
     * Sets the tracked benchmarks to the given list of benchmark names.
     * @param trackedBenchmarks The new tracked benchmarks.
     */
    void setTrackedBenchmarks(List<String> trackedBenchmarks) {
        this.trackedBenchmarks = trackedBenchmarks;
    }



    @Override
    public boolean equals(Object o) {
        boolean superEquals = super.equals(o);
        if (!superEquals) {
            return false;
        }

        LineDiagramDashboardModule otherModule = (LineDiagramDashboardModule) o;

        if (this.trackedRepositories.equals(otherModule.trackedRepositories)
            && this.trackedBenchmarks.equals(otherModule.trackedBenchmarks)) {
            return true;
        }
        return false;
    }
}
