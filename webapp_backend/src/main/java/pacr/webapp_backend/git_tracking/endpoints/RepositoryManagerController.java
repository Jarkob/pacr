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
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.NoSuchElementException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.http.MediaType;

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
    public RepositoryManagerController(@NotNull final GitTracking gitTracking, @NotNull final IAuthenticator authenticator) {
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
    public List<TransferRepository> getAllRepositories() {
        List<GitRepository> repositories = gitTracking.getAllRepositories();

        List<TransferRepository> transferRepositories = new ArrayList<>();
        for (GitRepository repository : repositories) {
            transferRepositories.add(createTransferRepository(repository));
        }

        return transferRepositories;
    }

    private TransferRepository createTransferRepository(final GitRepository gitRepository) {
        // convert selected branches to tracked branches
        final Set<String> branchNames = invertSet(gitRepository.getSelectedBranches(),
                gitTracking.getBranches(gitRepository.getPullURL()), gitRepository.isTrackAllBranches());

        // sort branch order
        List<String> trackedBranches = new ArrayList<>(branchNames);
        sortIgnoreCase(trackedBranches);

        return new TransferRepository(gitRepository.getId(),
                gitRepository.isTrackAllBranches(), trackedBranches, gitRepository.getPullURL(),
                gitRepository.getName(), gitRepository.isHookSet(), gitRepository.getColor(),
                gitRepository.getObserveFromDate(), gitRepository.getCommitLinkPrefix());
    }

    /**
     * @param pageable The requested page
     * @param repositoryID The id of the specified repository.
     * @return the requested page of commits in the repository.
     */
    @RequestMapping(value = "/commits/{repositoryID}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<GitCommit> getAllCommits(@PageableDefault(size = 50, page = 0, sort = "commitDate",
            direction = Sort.Direction.ASC) final Pageable pageable,
                                         @PathVariable("repositoryID") final int repositoryID) {
        return gitTracking.getAllCommits(repositoryID, pageable);
    }

    /**
     * Converts the tracked branches to selected branches.
     * @param subset is the set being inverted.
     * @param allEntries are all entries.
     * @param invert is whether the set should be inverted or not.
     * @return all selected branches that are on the white-/blacklist.
     */
    @NotNull
    private Set<String> invertSet(@NotNull final Set<String> subset,
                                  @NotNull final Set<String> allEntries, final boolean invert) {
        Objects.requireNonNull(subset);
        Objects.requireNonNull(allEntries);

        final Set<String> selectedBranches = new HashSet<>();

        if (invert) {
            // all branches are being tracked, repository is in blacklist mode
            selectedBranches.addAll(allEntries);
            for (final String trackedBranch : subset) {
                selectedBranches.remove(trackedBranch);
            }
        } else {
            // no branch is being tracked by default, repository is in whitelist mode
            selectedBranches.addAll(subset);
        }

        return selectedBranches;
    }

    /**
     * Adds a repository to the tracking.
     * @param transferRepository contains the arguments for the repository.
     * @return id of the repository.
     * @param token is the authentication token.
     */
    @PostMapping(value = "/add-repository")
    public int addRepository(@NotNull @RequestBody final TransferRepository transferRepository,
                             @NotNull @RequestHeader(name = "jwt") final String token) {
        Objects.requireNonNull(transferRepository);
        Objects.requireNonNull(token);

        if (!authenticator.authenticate(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        LOGGER.info("Adding repository {} with URL {}.", transferRepository.getName(),
                transferRepository.getPullURL());

        // convert tracked branches to selected branches
        Set<String> trackedBranches = new HashSet<>(transferRepository.getTrackedBranches());
        Set<String> selectedBranches = invertSet(trackedBranches,
                gitTracking.getBranches(transferRepository.getPullURL()), transferRepository.isTrackAllBranches());

        return gitTracking.addRepository(transferRepository.getPullURL(), transferRepository.getObserveFromDate(),
                transferRepository.getName(), selectedBranches,
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
    public ResponseEntity<Object> deleteRepository(@PathVariable("id") final int repositoryID,
                                                   @NotNull @RequestHeader(name = "jwt") final String token) {
        Objects.requireNonNull(token);
        if (!authenticator.authenticate(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        try {
            gitTracking.removeRepository(repositoryID);
        } catch (final NoSuchElementException e) {
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
    public void updateRepository(@NotNull @RequestBody final TransferRepository transferRepository,
                                 @NotNull @RequestHeader(name = "jwt") final String token) {
        Objects.requireNonNull(transferRepository);
        Objects.requireNonNull(token);
        if (!authenticator.authenticate(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        LOGGER.info("Updating repository {}.", transferRepository.getName());

        final GitRepository gitRepository = gitTracking.getRepository(transferRepository.getId());

        // add new color to color picker if necessary
        final String oldColor = gitRepository.getColor();
        final String newColor = transferRepository.getColor();

        if (!oldColor.equals(newColor)) {
            LOGGER.info("Changing color from {} to {}.", oldColor, newColor);
            gitTracking.updateColorOfRepository(gitRepository, newColor);
        }

        // change observeFromDate if necessary
        final LocalDate oldObserveFromDate = gitRepository.getObserveFromDate();
        final LocalDate newObserveFromDate = transferRepository.getObserveFromDate();

        if ((oldObserveFromDate != newObserveFromDate)
                || (oldObserveFromDate != null && !oldObserveFromDate.isEqual(newObserveFromDate))) {

            LOGGER.info("Changing observeFromDate from {} to {}.", oldObserveFromDate, newObserveFromDate);
            gitTracking.updateObserveFromDateOfRepository(gitRepository, newObserveFromDate);
        }

        // convert tracked branches to selected branches
        Set<String> trackedBranches = new HashSet<>(transferRepository.getTrackedBranches());
        Set<String> selectedBranches = invertSet(trackedBranches,
                gitTracking.getBranches(transferRepository.getPullURL()), transferRepository.isTrackAllBranches());

        gitRepository.setSelectedBranches(selectedBranches);
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
    public List<String> getBranchesFromRepository(@RequestBody String pullURL) {
        LOGGER.info("Getting branches from repository with pull url {}.", pullURL);

        List<String> branches = new ArrayList<>(gitTracking.getBranches(pullURL));
        sortIgnoreCase(branches);

        return branches;
    }

    private void sortIgnoreCase(List<String> list) {
        list.sort(Comparator.comparing(String::toLowerCase));
    }

}
