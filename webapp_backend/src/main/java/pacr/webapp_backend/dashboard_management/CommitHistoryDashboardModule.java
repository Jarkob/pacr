package pacr.webapp_backend.dashboard_management;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class represent commit history modules on a dashboard.
 * These modules show the commit history for a list of repositories.
 *
 * @author Benedikt Hahn
 */
@Entity
public class CommitHistoryDashboardModule extends DashboardModule {

    @ElementCollection(fetch = FetchType.EAGER)
    List<String> trackedRepositories = new ArrayList<>();

    /**
     * Public no argument constructor for jpa.
     */
    public CommitHistoryDashboardModule() {

    }

    /**
     * Sets the tracked repositories to the given list of repository names.
     *
     * @param trackedRepositories The new tracked repositories.
     */
    public void setTrackedRepositories(final List<String> trackedRepositories) {
        this.trackedRepositories = List.copyOf(trackedRepositories);
    }

    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }

        final CommitHistoryDashboardModule otherModule = (CommitHistoryDashboardModule) o;

        if (this.trackedRepositories.size() != otherModule.trackedRepositories.size()) {
            return false;
        }

        for (final String repo : this.trackedRepositories) {
            if (!otherModule.trackedRepositories.contains(repo)) {
                return false;
            }
        }

        return true;
    }

}
