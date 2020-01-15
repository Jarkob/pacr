package pacr.webapp_backend.git_tracking.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.git_tracking.services.IPullIntervalAccess;

/**
 * Represents the pull interval controller.
 * Provides a RequestMapping for getting the pull interval
 * and a PutMapping for setting the pull interval.
 */
@RestController
public class PullIntervalController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PullIntervalController.class);

    private IPullIntervalAccess pullIntervalAccess;

    /**
     * Creates a new instance of PullIntervalController.
     * @param pullIntervalAccess is the database access.
     */
    public PullIntervalController(IPullIntervalAccess pullIntervalAccess) {
        this.pullIntervalAccess = pullIntervalAccess;
    }

    /**
     * Gets the pull interval.
     * @return pull interval.
     */
    @RequestMapping("/pull-interval")
    public int getPullInterval() {
        return pullIntervalAccess.getPullInterval();
    }

    /**
     * Sets the pull interval.
     * @param interval is the pull interval.
     * @return HTTP Response OK (200) if the pull interval was set correctly.
     */
    @PutMapping("pull-interval/{interval}")
    public ResponseEntity<Object> setPullInterval(@PathVariable int interval) {
        pullIntervalAccess.setPullInterval(interval);
        LOGGER.info("Changed pull interval to {} seconds.", interval);

        return ResponseEntity.ok().build();
    }

}
