package pacr.webapp_backend.dashboard_management.endpoints;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.services.DashboardManager;

import javax.validation.constraints.NotNull;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Provides a REST interface for accessing, storing and updating dashboards.
 */
@RestController
public class ManageDashboardController {

    DashboardManager dashboardManager;


    /**
     * Creates a new ManageDashboardController
     *
     * @param dashboardManager The {@link DashboardManager}, which proivdes access to the functions of the dashboard
     *                         component.
     */
    public ManageDashboardController(@NotNull final DashboardManager dashboardManager) {
        Objects.requireNonNull(dashboardManager, "The dashboard manager must not be null.");
        this.dashboardManager = dashboardManager;
    }


    /**
     * Returns a dashboard to a specified key.
     *
     * @param key The key, of the dashboard.
     * @return A {@link ResponseEntity} containing the requested dashboard, if the key is valid;
     * otherwise an exception response.
     */
    @GetMapping("dashboard/{key}")
    public ResponseEntity<Object> getDashboard(@PathVariable @NotNull final String key) {
        Objects.requireNonNull(key, "The key must not be null.");

        try {
            final Dashboard dashboard = dashboardManager.getDashboard(key);
            return ResponseEntity.ok(dashboard);
        } catch (final NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Adds the given dashboard to the backend.
     *
     * @param dashboard the dashboard to be stored.
     * @return HTTP code 200 (ok) if the dashboard was added.
     */
    @PutMapping("dashboard/add")
    public ResponseEntity<Object> addDashboard(@RequestBody @NotNull final Dashboard dashboard) {
        Objects.requireNonNull(dashboard);

        final Pair<String, String> keys;

        try {
            keys = dashboardManager.addDashboard(dashboard);
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok().body(keys);
    }

    /**
     * Updates the specified dashboard in the backend.
     *
     * @param dashboard the new dashboard.
     * @return HTTP code 200 (ok) if the dashboard was updated. HTTP code 404 (not found) if no dashboard with the given
     * keys could be found. HTTP code 401 (unauthorized) if the dashboard does not contain a valid edit key.
     */
    @PutMapping("dashboard/update")
    public ResponseEntity<Object> updateDashboard(@RequestBody @NotNull final Dashboard dashboard) {
        try {
            dashboardManager.updateDashboard(dashboard);
        } catch (final IllegalAccessException e) {
            return ResponseEntity.status((HttpStatus.UNAUTHORIZED)).body(e.getMessage());
        } catch (final NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
            return ResponseEntity.ok().build();
    }

    /**
     * Deletes the dashboard with the specified edit key.
     *
     * @param editKey the edit key of the dashboard to delete.
     * @return HTTP code 200 (ok) if the dashboard was deleted. HTTP code 404 (not found) if no dashboard with the given
     * key could be found. HTTP code 401 (unauthorized) if the key is not an edit key.
     */
    @DeleteMapping("dashboard/delete/{editKey}")
    public ResponseEntity<Object> deleteDashboard(@PathVariable @NotNull final String editKey) {
        try {
            dashboardManager.deleteDashboard(editKey);
        } catch (final IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (final NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
