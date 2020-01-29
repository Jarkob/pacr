package pacr.webapp_backend.git_tracking.endpoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.services.GitTracking;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import java.awt.*;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for managing repositories.
 * This is an entry point for adding, updating, getting deleting repositories.
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
            for (GitBranch branch : repository.getTrackedBranches()) {
                branchNames.add(branch.getName());
            }

            // remove bits 24-31 because alpha channel is not needed
            int color = repository.getColor().getRGB() & 0xFFFFFF;
            String colorStr = "#" + Integer.toHexString(color);

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

        Map<String, Boolean> selectedBranches = new HashMap<>();
        for (String branch : transferRepository.getSelectedBranches()) {
            selectedBranches.put(branch, Boolean.TRUE);
        }

        return gitTracking.addRepository(transferRepository.getPullURL(), transferRepository.getObserveFromDate(),
                transferRepository.getName(), selectedBranches);
    }

    /**
     * Deletes a repository.
     * @param repositoryID is the ID of the repository.
     * @return OK (200) if the repository was deleted successfully,
     *         NOT_FOUND (404) if the repository was not found.
     */
    @DeleteMapping(value = "/deleteRepository/{id}")
    public ResponseEntity<Object> deleteRepository(@PathVariable("id") int repositoryID) {

        try {
            gitTracking.removeRepository(repositoryID);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Updates a repository.
     * @param transferRepository contains the new values for the repository.
     */
    @PostMapping(value = "/updateRepository")
    public void updateRepository(@RequestBody TransferRepository transferRepository) {
        LOGGER.info("Updating repository {}.", transferRepository.getName());

        GitRepository gitRepository = gitTracking.getRepository(transferRepository.getId());

        // add new color to color picker if necessary
        Color oldColor = gitRepository.getColor();
        Color newColor = Color.getColor(transferRepository.getColor());
        if (!oldColor.equals(newColor)) {
            gitTracking.updateColorOfRepository(gitRepository, newColor);
        }

        // change selected branches
        Map<String, Boolean> selectedBranches = gitRepository.getSelectedBranches();
        // set all selected branches to false
        selectedBranches.replaceAll((b, v) -> Boolean.FALSE);
        // set new selected branches to true
        for (String branch : transferRepository.getSelectedBranches()) {
            if (selectedBranches.containsKey(branch)) {
                selectedBranches.put(branch, Boolean.TRUE);
            }
        }

        gitRepository.setTrackAllBranches(transferRepository.isTrackAllBranches());
        gitRepository.setPullURL(transferRepository.getPullURL());
        gitRepository.setName(transferRepository.getName());
        gitRepository.setIsHookSet(transferRepository.isHookSet());
        gitRepository.setObserveFromDate(transferRepository.getObserveFromDate());
        gitRepository.setCommitLinkPrefix(transferRepository.getCommitLinkPrefix());

        gitTracking.updateRepository(gitRepository);
    }

    /**
     * Gets all branches of a specific repository.
     * @param repositoryID is the ID of the repository.
     * @return all branches of the repository.
     */
    @RequestMapping(value = "/branches/{id}")
    public Set<String> getBranchesFromRepository(@PathVariable("id") int repositoryID) {
        LOGGER.info("Getting branches from repository with ID {}.", repositoryID);

        GitRepository repository = gitTracking.getRepository(repositoryID);
        if (repository == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }


        return repository.getSelectedBranches().keySet();
    }


}
