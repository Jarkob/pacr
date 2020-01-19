package pacr.webapp_backend.dashboard_management;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

/**
 * Instances of this class represent commit history modules on a dashboard.
 * These modules show the commit history for a list of repositories.
 *
 * @author Benedikt Hahn
 */
@Entity
public class CommitHistoryDashboardModule extends DashboardModule {

    @ElementCollection
    List<String> trackedRepositories;

    /**
     * Sets the tracked repositories to the given list of repository names.
     * @param trackedRepositories The new tracked repositories.
     */
     void setTrackedRepositories(List<String> trackedRepositories) {
        this.trackedRepositories = trackedRepositories;
    }

    @Override
    public boolean equals(Object o) {
        boolean superEquals = super.equals(o);
        if (!superEquals) {
            return false;
        }

        LineDiagramDashboardModule otherModule = (LineDiagramDashboardModule) o;

        if (this.trackedRepositories.equals(otherModule.trackedRepositories)) {
            return true;
        }
        return false;
    }

}
