package pacr.webapp_backend.dashboard_management;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * Instances of this class represent event modules on a dashboard, which show the newest events on the RSS-Feed.
 * Event modules can't be further configured.
 *
 * @author Benedikt Hahn
 */
@Entity
@NoArgsConstructor
public class EventDashboardModule extends DashboardModule {
}
