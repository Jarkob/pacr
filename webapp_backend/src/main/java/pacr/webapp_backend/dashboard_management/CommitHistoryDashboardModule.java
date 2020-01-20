package pacr.webapp_backend.dashboard_management;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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

    @ElementCollection
    List<String> trackedRepositories = new ArrayList<>();

    /**
     * Public no argument constructor for jpa.
     */
    public CommitHistoryDashboardModule() {

    }

    /**
     * Creates a new module, with an initial position.
     * @param position the initial position.
     */
    CommitHistoryDashboardModule(int position) {
        super(position);
    }

    /**
     * Sets the tracked repositories to the given list of repository names.
     * @param trackedRepositories The new tracked repositories.
     */
     void setTrackedRepositories(List<String> trackedRepositories) {
        this.trackedRepositories = trackedRepositories;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        CommitHistoryDashboardModule otherModule = (CommitHistoryDashboardModule) o;

        return this.trackedRepositories.equals(otherModule.trackedRepositories);
    }

}
