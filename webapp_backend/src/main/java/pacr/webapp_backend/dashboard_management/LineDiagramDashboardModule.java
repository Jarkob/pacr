package pacr.webapp_backend.dashboard_management;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Instances of this class represent line diagram modules on a dashboard.
 * One of these leaderboard modules shows the line diagram for specific benchmarks and repositories.
 *
 * @author Benedikt Hahn
 */
@Entity
public class LineDiagramDashboardModule extends DashboardModule {

    @ElementCollection(fetch = FetchType.EAGER)
    Set<String> trackedRepositories = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    List<String> trackedBenchmarks = new ArrayList<>();


    /**
     * Public no argument constructor for jpa.
     */
    public LineDiagramDashboardModule() {

    }


    /**
     * Sets the tracked repositories to the given list of repository names.
     *
     * @param trackedRepositories The new tracked repositories.
     */
    public void setTrackedRepositories(final List<String> trackedRepositories) {
        this.trackedRepositories = Set.copyOf(trackedRepositories);
    }

    /**
     * Sets the tracked benchmarks to the given list of benchmark names.
     *
     * @param trackedBenchmarks The new tracked benchmarks.
     */
    public void setTrackedBenchmarks(final List<String> trackedBenchmarks) {
        this.trackedBenchmarks = List.copyOf(trackedBenchmarks);
    }


    @Override
    public boolean equals(final Object o) {
        final boolean superEquals = super.equals(o);
        if (!superEquals) {
            return false;
        }

        final LineDiagramDashboardModule otherModule = (LineDiagramDashboardModule) o;

        if (this.trackedRepositories.size() != otherModule.trackedRepositories.size()) {
            return false;
        }

        for (final String repo : this.trackedRepositories) {
            if (!otherModule.trackedRepositories.contains(repo)) {
                return false;
            }
        }

        if (this.trackedBenchmarks.size() != otherModule.trackedBenchmarks.size()) {
            return false;
        }

        for (final String benchmarkName : this.trackedBenchmarks) {
            if (!otherModule.trackedBenchmarks.contains(benchmarkName)) {
                return false;
            }
        }
        return true;
    }
}
