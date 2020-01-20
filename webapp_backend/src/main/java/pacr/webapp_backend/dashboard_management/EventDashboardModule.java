package pacr.webapp_backend.dashboard_management;

import pacr.webapp_backend.dashboard_management.DashboardModule;

import javax.persistence.Entity;

/**
 * Instances of this class represent event modules on a dashboard, which show the newest events on the RSS-Feed.
 * Event modules can't be further configured.
 *
 * @author Benedikt Hahn
 */
@Entity
public class EventDashboardModule extends DashboardModule {

    /**
     * Public no argument constructor for jpa.
     */
    public EventDashboardModule() {

    }

    /**
     * Creates a new module, with an initial position.
     * @param position the initial position.
     */
    EventDashboardModule(int position) {
        super(position);
    }

}
