package pacr.webapp_backend.git_tracking.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.services.GitTracking;

import java.util.NoSuchElementException;

/**
 * Controller for web hooks. Can pull from repositories.
 *
 * @author Pavel Zwerschke
 */
@RestController
public class WebHookController {

    private GitTracking gitTracking;

    /**
     * Initializes an instance of WebHookController.
     * @param gitTracking is the needed GitTracking.
     */
    public WebHookController(GitTracking gitTracking) {
        this.gitTracking = gitTracking;
    }

    /**
     * Pulls from a repository.
     * @param repositoryID is the ID of the repository.
     * @return OK (200) when the repository got pulled. INTERNAL_SERVER_ERROR (500) when the repository was not found.
     */
    @RequestMapping(value = "/webhooks/{id}")
    public ResponseEntity<Object> pullFromRepository(@PathVariable("id") int repositoryID) {
        try {
            gitTracking.pullFromRepository(repositoryID);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();

    }

}
