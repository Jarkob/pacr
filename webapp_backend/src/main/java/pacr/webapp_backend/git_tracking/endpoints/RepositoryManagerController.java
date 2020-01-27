package pacr.webapp_backend.git_tracking.endpoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.services.GitTracking;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for managing repositories.
 *
 * @author Pavel Zwerschke
 */
@RestController
public class RepositoryManagerController {

    private static final Logger LOGGER = LogManager.getLogger(RepositoryManagerController.class);

    private GitTracking gitTracking;

    /**
     * Creates an instance of RepositoryManagerController.
     * @param gitTracking is the GitTracking component needed to manage the repositories.
     */
    public RepositoryManagerController(GitTracking gitTracking) {
        this.gitTracking = gitTracking;
    }

    /**
     * Returns all repositories.
     * @return all repositories in JSON.
     */
    @RequestMapping(value = "/allRepositories", method = RequestMethod.GET,
    produces = APPLICATION_JSON_VALUE)
    public Set<TransferRepository> getAllRepositories() {
        Set<GitRepository> repositories = gitTracking.getAllRepositories();

        Set<TransferRepository> transferRepositories = new HashSet<>();
        for (GitRepository repository : repositories) {
            Set<String> branchNames = new HashSet<>();
            for (GitBranch branch : repository.getSelectedBranches()) {
                branchNames.add(branch.getName());
            }

            int color = repository.getColor().getRGB();
            String colorStr = Integer.toHexString(color);

            TransferRepository transferRepository = new TransferRepository(repository.getId(),
                    repository.isTrackAllBranches(), branchNames, repository.getPullURL(),
                    repository.getName(), repository.isHookSet(), colorStr,
                    repository.getObserveFromDate(), repository.getCommitLinkPrefix());

            transferRepositories.add(transferRepository);
        }

        return transferRepositories;
    }

    /**
     * Adds a repository to the tracking.
     * @param transferRepository contains the arguments for the repository.
     * @return id of the repository.
     */
    @PostMapping(value = "/addRepository")
    public int addRepository(@RequestBody TransferRepository transferRepository) {

        LOGGER.info("Adding repository {} with URL {}.", transferRepository.getName(),
                transferRepository.getPullURL());

        return gitTracking.addRepository(transferRepository.getPullURL(), transferRepository.getObserveFromDate(),
                transferRepository.getName(), transferRepository.getSelectedBranches());
    }

    /**
     * Deletes a repository.
     * @param repositoryID is the ID of the repository.
     * @return OK (200) if the repository was deleted successfully,
     *         BAD_REQUEST (400) if the repository was not found.
     */
    @DeleteMapping(value = "/deleteRepository/{id}")
    public ResponseEntity<Object> pullFromRepository(@PathVariable("id") int repositoryID) {

        try {
            gitTracking.removeRepository(repositoryID);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

}
