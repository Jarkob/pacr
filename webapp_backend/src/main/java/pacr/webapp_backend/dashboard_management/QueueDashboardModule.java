package pacr.webapp_backend.dashboard_management;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * Instances of this class represent queue modules on a dashboard and show the complete queue.
 * Queue modules can't be further configured.
 *
 * @author Benedikt Hahn
 */
@Entity
@NoArgsConstructor
public class QueueDashboardModule extends DashboardModule {

}
