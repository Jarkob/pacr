package pacr.webapp_backend.dashboard_management;

import javax.persistence.Entity;

/**
 * Instances of this class represent queue modules on a dashboard and show the complete queue.
 * Queue modules can't be further configured.
 *
 * @author Benedikt Hahn
 */
@Entity
public class QueueDashboardModule extends DashboardModule {

    /**
     * Public no argument constructor for jpa.
     */
    public QueueDashboardModule() {

    }

    /**
     * Creates a new module, with an initial position.
     * @param position the initial position.
     */
    QueueDashboardModule(int position) {
        super(position);
    }

}
