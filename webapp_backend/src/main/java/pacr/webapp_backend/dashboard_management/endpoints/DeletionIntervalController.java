package pacr.webapp_backend.dashboard_management.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.dashboard_management.services.DashboardManager;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Provides a REST interface to access and update the deletion interval.
 */
@RestController
public class DeletionIntervalController {

    private DashboardManager dashboardManager;
    private IAuthenticator authenticator;

    private static final  String INVALID_TOKEN_MESSAGE = "The given http token is not valid.";


    /**
     * Creates a new DeletionIntervalController.
     *
     * @param dashboardManager the dashboard manager, used for entry into the services .
     * @param authenticator the authenticator, which checks whether admin requests are authorized.
     */
    @Autowired
    public DeletionIntervalController(@NotNull DashboardManager dashboardManager,
                                      @NotNull IAuthenticator authenticator) {
        Objects.requireNonNull(dashboardManager, "The dashboard manager must not be null.");
        Objects.requireNonNull(authenticator, "The authenticator must not be null.");

        this.dashboardManager = dashboardManager;
        this.authenticator = authenticator;
    }


    /**
     * Returns the current dashboard deletion interval.
     * @return the deletion interval in days.
     */
    @GetMapping("deletion-interval")
    public ResponseEntity<Long> getDeletionInterval() {
        long deletionInterval = dashboardManager.getDeletionInterval();

        return ResponseEntity.ok(deletionInterval);
    }


    /**
     * Sets the deletion interval to the given
     * @param deletionInterval the deletion interval, which should be set.
     * @param token the http token, with which the request is started.
     * @return a http response, whether the action was successful.
     */
    @PutMapping("deletion-interval/{deletionInterval}")
    public ResponseEntity<Object> changeDeletionInterval(@PathVariable long deletionInterval,
                                                         @NotNull @RequestHeader(name = "jwt") String token) {

        Objects.requireNonNull(token, "The token must not be null.");

        if (authenticator.authenticate(token)) {
            try {
                dashboardManager.setDeletionInterval(deletionInterval);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }

            return ResponseEntity.ok().build();

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_TOKEN_MESSAGE);
    }

}
