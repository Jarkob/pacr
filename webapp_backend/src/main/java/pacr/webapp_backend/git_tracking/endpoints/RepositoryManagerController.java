package pacr.webapp_backend.git_tracking.endpoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.services.GitTracking;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.HashSet;

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
    private IAuthenticator authenticator;

    /**
     * Creates an instance of RepositoryManagerController.
     * @param gitTracking is the GitTracking component needed to manage the repositories.
     * @param authenticator is the authenticator for checking the token.
     */
    public RepositoryManagerController(@NotNull GitTracking gitTracking, @NotNull IAuthenticator authenticator) {
        Objects.requireNonNull(gitTracking);
        Objects.requireNonNull(authenticator);
        this.gitTracking = gitTracking;
        this.authenticator = authenticator;
    }

    /**
     * Returns all repositories.
     * @return all repositories in JSON.
     */
    @RequestMapping(value = "/repositories", method = RequestMethod.GET,
    produces = APPLICATION_JSON_VALUE)
    public Set<TransferRepository> getAllRepositories() {
        Set<GitRepository> repositories = gitTracking.getAllRepositories();

        Set<TransferRepository> transferRepositories = new HashSet<>();
        for (GitRepository repository : repositories) {
            Set<String> branchNames = new HashSet<>();
            for (GitBranch branch : repository.getTrackedBranches()) {
                branchNames.add(branch.getName());
            }

            TransferRepository transferRepository = new TransferRepository(repository.getId(),
                    repository.isTrackAllBranches(), branchNames, repository.getPullURL(),
                    repository.getName(), repository.isHookSet(), repository.getColor(),
                    repository.getObserveFromDate(), repository.getCommitLinkPrefix());

            transferRepositories.add(transferRepository);
        }

        return transferRepositories;
    }

    @RequestMapping(value = "/commits/{repositoryID}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public Page<GitCommit> getAllCommits(@PageableDefault(size = 50, page = 0, sort = {"commitDate"},
            direction = Sort.Direction.ASC) Pageable pageable, @PathVariable("repositoryID") int repositoryID) {
        return gitTracking.getAllCommits(repositoryID, pageable);
    }

    /**
     * Adds a repository to the tracking.
     * @param transferRepository contains the arguments for the repository.
     * @return id of the repository.
     * @param token is the authentication token.
     */
    @PostMapping(value = "/add-repository")
    public int addRepository(@NotNull @RequestBody TransferRepository transferRepository,
                             @NotNull @RequestHeader(name = "jwt") String token) {
        Objects.requireNonNull(transferRepository);
        Objects.requireNonNull(token);

        if (!authenticator.authenticate(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        LOGGER.info("Adding repository {} with URL {}.", transferRepository.getName(),
                transferRepository.getPullURL());

        return gitTracking.addRepository(transferRepository.getPullURL(), transferRepository.getObserveFromDate(),
                transferRepository.getName(), transferRepository.getSelectedBranches(),
                transferRepository.isTrackAllBranches(), transferRepository.isHookSet());
    }

    /**
     * Deletes a repository.
     * @param repositoryID is the ID of the repository.
     * @param token is the authentication token.
     * @return OK (200) if the repository was deleted successfully,
     *         NOT_FOUND (404) if the repository was not found,
     *         UNAUTHORIZED (401) if the access is unauthorized.
     */
    @DeleteMapping(value = "/delete-repository/{id}")
    public ResponseEntity<Object> deleteRepository(@PathVariable("id") int repositoryID,
                                                   @NotNull @RequestHeader(name = "jwt") String token) {
        Objects.requireNonNull(token);
        if (!authenticator.authenticate(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        try {
            gitTracking.removeRepository(repositoryID);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found.");
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Updates a repository.
     * @param transferRepository contains the new values for the repository.
     * @param token is the authentication token.
     */
    @PostMapping(value = "/update-repository")
    public void updateRepository(@NotNull @RequestBody TransferRepository transferRepository,
                                 @NotNull @RequestHeader(name = "jwt") String token) {
        Objects.requireNonNull(transferRepository);
        Objects.requireNonNull(token);
        if (!authenticator.authenticate(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        LOGGER.info("Updating repository {}.", transferRepository.getName());

        GitRepository gitRepository = gitTracking.getRepository(transferRepository.getId());

        // add new color to color picker if necessary
        String oldColor = gitRepository.getColor();
        String newColor = transferRepository.getColor();
        if (!oldColor.equals(newColor)) {
            LOGGER.info("Changing color from {} zo {}.", oldColor, newColor);
            gitTracking.updateColorOfRepository(gitRepository, newColor);
        }

        LocalDate oldObserveFromDate = gitRepository.getObserveFromDate();
        LocalDate newObserveFromDate = transferRepository.getObserveFromDate();
        if ((oldObserveFromDate == null && newObserveFromDate != null)
                || (oldObserveFromDate != null && newObserveFromDate == null)
                || (oldObserveFromDate != null && !oldObserveFromDate.isEqual(newObserveFromDate))) {
            LOGGER.info("Changing observeFromDate from {} to {}.", oldObserveFromDate, newObserveFromDate);
            gitTracking.updateObserveFromDateOfRepository(gitRepository, newObserveFromDate);
        }

        gitRepository.setSelectedBranches(transferRepository.getSelectedBranches());

        gitRepository.setTrackAllBranches(transferRepository.isTrackAllBranches());
        gitRepository.setPullURL(transferRepository.getPullURL());
        gitRepository.setName(transferRepository.getName());
        gitRepository.setIsHookSet(transferRepository.isHookSet());
        gitRepository.setObserveFromDate(transferRepository.getObserveFromDate());

        gitTracking.updateRepository(gitRepository);
    }

    /**
     * Gets all branches of a specific repository.
     * @param pullURL is the pull url of the repository.
     * @return all branches of the repository.
     */
    @PostMapping(value = "/branches")
    public Set<String> getBranchesFromRepository(@RequestBody String pullURL) {
        LOGGER.info("Getting branches from repository with pull url {}.", pullURL);

        return gitTracking.getBranches(pullURL);
    }

}
