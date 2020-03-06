package pacr.webapp_backend.dashboard_management;

import lombok.NoArgsConstructor;

/**
 * Gets thrown, when a dashboard is full, so you can't add more elements to it.
 */
@NoArgsConstructor
public class DashboardFullException extends RuntimeException {
}
