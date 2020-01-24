package pacr.webapp_backend.dashboard_management.services;

import pacr.webapp_backend.dashboard_management.Dashboard;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

/**
 * This class contains classes for generating unique edit and view keys.
 * These keys are used as identifiers, for authorizing edit and view access.
 *
 * @author Benedikt Hahn
 */
public class KeyManager {

    /**
     * Generates and sets a unique edit key for a given dashboard.
     * @param dashboard The dashboard for which the edit key should be generated.
     */
    static void generateEditKey(@NotNull Dashboard dashboard) {
        Objects.requireNonNull(dashboard, "The dashboard must not be null!");
        String editKey = UUID.randomUUID().toString();
        dashboard.setEditKey(editKey);
    }

    /**
     * Generates and sets a unique view key for a given dashboard.
     * @param dashboard The dashboard for which the view key should be generated.
     */
    static void generateViewKey(@NotNull Dashboard dashboard) {
        Objects.requireNonNull(dashboard, "The dashboard must not be null!");
        String viewKey = UUID.randomUUID().toString();
        dashboard.setViewKey(viewKey);
    }
}
