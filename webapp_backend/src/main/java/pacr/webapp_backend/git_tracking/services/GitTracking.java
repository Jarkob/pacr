package pacr.webapp_backend.git_tracking.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.git.GitHandler;
import pacr.webapp_backend.git_tracking.services.git.PullFromRepositoryException;
import pacr.webapp_backend.shared.ICommitBenchmarkedChecker;
import pacr.webapp_backend.shared.IJobScheduler;
import pacr.webapp_backend.shared.IRepositoryImporter;
import pacr.webapp_backend.shared.IResultDeleter;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

/**
 * Represents the Git Tracking component of the system.
 * Manages repositories, commits, branches and can pull from them.
 *
 * @author Pavel Zwerschke
 */
@Component
public class GitTracking implements IRepositoryImporter {

    private static final Logger LOGGER = LogManager.getLogger(GitTracking.class);

    private IGitTrackingAccess gitTrackingAccess;
    private IResultDeleter resultDeleter;
    private IJobScheduler jobScheduler;
    private IColorPicker colorPicker;
    private GitHandler gitHandler;
    private ICommitBenchmarkedChecker commitBenchmarkedChecker;
    private String ignoreTag;

    private boolean pullingFromAllRepositories;

    /**
     * Initiates an instance of GitTracking.
     * @param gitTrackingAccess is the database access for repositories and commits.
     * @param gitHandler is the interface that manages the JGit integration.
     * @param resultDeleter is the interface that deletes benchmarking results.
     * @param jobScheduler is the interface that adds job to the benchmarking queue.
     * @param colorPicker is the algorithm that assigns new colors to repositories.
     * @param commitBenchmarkedChecker checks whether a commit is already benchmarked or not.
     * @param ignoreTag is the pacr ignore tag.
     */
    public GitTracking(@NotNull IGitTrackingAccess gitTrackingAccess, @NotNull GitHandler gitHandler,
                       @NotNull IResultDeleter resultDeleter, @NotNull IJobScheduler jobScheduler,
                       @NotNull IColorPicker colorPicker, @NotNull ICommitBenchmarkedChecker commitBenchmarkedChecker,
                       @NotNull @Value("${ignoreTag}") String ignoreTag) {
        Objects.requireNonNull(gitTrackingAccess);
        Objects.requireNonNull(gitHandler);
        Objects.requireNonNull(resultDeleter);
        Objects.requireNonNull(jobScheduler);
        Objects.requireNonNull(colorPicker);
        Objects.requireNonNull(commitBenchmarkedChecker);
        Objects.requireNonNull(ignoreTag);

        this.gitTrackingAccess = gitTrackingAccess;
        this.gitHandler = gitHandler;
        this.resultDeleter = resultDeleter;
        this.jobScheduler = jobScheduler;
        this.colorPicker = colorPicker;
        this.commitBenchmarkedChecker = commitBenchmarkedChecker;
        this.ignoreTag = ignoreTag;
        this.pullingFromAllRepositories = false;
    }

    /**
     * Sets all colors of the repositories to used.
     */
    @PostConstruct
    private void initializeColorPicker() {
        List<GitRepository> repositories = gitTrackingAccess.getAllRepositories();

        for (GitRepository repository : repositories) {
            colorPicker.setColorToUsed(repository.getColor());
        }
    }

    /**
     * Adds a repository.
     * @param repositoryURL is the pull URL of the repository.
     * @param observeFromDate is the date from when it should be observed.
     * @param name is the name of the repository.
     * @param branchNames are the names of the selected branches.
     * @param trackAllBranches whether all branches are being tracked or just master branch.
     * @param isHookSet whether a hook is set.
     * @return the ID of the repository.
     */
    public int addRepository(@NotNull String repositoryURL, LocalDate observeFromDate, @NotNull String name,
                             @NotNull Set<String> branchNames, boolean trackAllBranches, boolean isHookSet) {
        Objects.requireNonNull(repositoryURL);
        Objects.requireNonNull(name);

        GitRepository repository = new GitRepository();
        repository.setPullURL(repositoryURL);
        repository.setName(name);
        repository.setObserveFromDate(observeFromDate);
        repository.setSelectedBranches(branchNames);
        repository.setColor(colorPicker.getNextColor());
        repository.setTrackAllBranches(trackAllBranches);
        repository.setIsHookSet(isHookSet);

        return gitTrackingAccess.addRepository(repository);
    }

    @Override
    public int importRepository(@NotNull String repositoryURL, LocalDate observeFromDate, @NotNull String name,
                                @NotNull Set<String> branchNames) {
        int id = addRepository(repositoryURL, observeFromDate, name, branchNames, false, false);
        pullFromRepository(id);

        return id;
    }

    /**
     * Gets all commits belonging to the repository in pages.
     * @param pageable contains information about the page.
     * @param repositoryID the id of the repository.
     * @return a page of commits.
     */
    public Page<GitCommit> getAllCommits(int repositoryID, Pageable pageable) {
        return gitTrackingAccess.getAllCommits(repositoryID, pageable);
    }

    /**
     * Returns all repositories.
     * @return collection of repositories.
     */
    public List<GitRepository> getAllRepositories() {
        return gitTrackingAccess.getAllRepositories();
    }

    /**
     * Removes a repository.
     * @param repositoryID is the ID of the repository.
     * @throws NoSuchElementException if the repository was not found in the database.
     */
    public void removeRepository(int repositoryID) throws NoSuchElementException {
        LOGGER.info("Removing repository with ID {}.", repositoryID);

        GitRepository repository = gitTrackingAccess.getRepository(repositoryID);
        if (repository == null) {
            throw new NoSuchElementException("Repository with ID " + repositoryID + " was not found.");
        }

        Set<String> commitHashes = gitTrackingAccess.getAllCommitHashes(repositoryID);

        resultDeleter.deleteBenchmarkingResults(commitHashes);
        jobScheduler.removeJobGroup(repository.getPullURL());

        gitTrackingAccess.removeCommits(commitHashes);

        gitTrackingAccess.removeRepository(repositoryID);
    }

    /**
     * Updates a repository.
     * @param repository is the repository being updated.
     * @throws NoSuchElementException if the repository was not found in the database.
     */
    public void updateRepository(@NotNull GitRepository repository) throws NoSuchElementException {
        Objects.requireNonNull(repository);

        gitTrackingAccess.updateRepository(repository);
    }

    /**
     * Pulls from a repository and adds new commits to the job scheduling.
     * @param repositoryID is the ID of the repository.
     * @throws NoSuchElementException when the repository was not found.
     */
    public synchronized void pullFromRepository(int repositoryID) throws NoSuchElementException {
        GitRepository gitRepository = gitTrackingAccess.getRepository(repositoryID);
        if (gitRepository == null) {
            throw new NoSuchElementException("Repository with ID " + repositoryID + " was not found.");
        }

        gitHandler.setBranchesToRepo(gitRepository);
        gitTrackingAccess.updateRepository(gitRepository);

        gitRepository = gitTrackingAccess.getRepository(repositoryID);

        Collection<String> untrackedCommitHashes;
        try {
            untrackedCommitHashes = gitHandler.pullFromRepository(gitRepository);
        } catch (PullFromRepositoryException e) {
            return;
        }

        LOGGER.info("Got {} untracked commits.", untrackedCommitHashes.size());

        // automatically adds all new commits to the database
        gitTrackingAccess.updateRepository(gitRepository);

        LOGGER.info("Adding jobs for {} untracked commits.", untrackedCommitHashes.size());

        // add jobs to scheduler
        jobScheduler.addJobs(gitRepository.getPullURL(), untrackedCommitHashes);

        LOGGER.info("Finished with pulling from repository {} ({}).", gitRepository.getName(), repositoryID);
    }

    /**
     * Pulls from all repositories.
     */
    public void pullFromAllRepositories() {
        this.pullingFromAllRepositories = true;

        for (GitRepository repository : gitTrackingAccess.getAllRepositories()) {
            LOGGER.info("Checking if hook is set for {} ({}).", repository.getName(), repository.getId());
            if (!repository.isHookSet()) {
                LOGGER.info("Trying to pull from repository {} ({}).", repository.getName(), repository.getId());
                pullFromRepository(repository.getId());
            }
        }

        this.pullingFromAllRepositories = false;
    }

    /**
     * Updates the color of a repository.
     * @param repository is the repository.
     * @param color is the color of the repository.
     */
    public void updateColorOfRepository(GitRepository repository, String color) {
        this.colorPicker.setColorToUnused(repository.getColor());
        this.colorPicker.setColorToUsed(color);
        repository.setColor(color);
    }

    /**
     * Returns a repository.
     * @param id is the ID of the repository.
     * @return repository.
     */
    public GitRepository getRepository(int id) {
        return gitTrackingAccess.getRepository(id);
    }

    /**
     * @param pullURL the pull URL of the repository.
     * @return all branches of the repository.
     */
    public Set<String> getBranches(String pullURL) {
        return gitHandler.getBranchesOfRepository(pullURL);
    }

    /**
     * Updates observeFromDate of a repository.
     * Removes all commit benchmarks that are not in the scope anymore
     * and adds jobs for all commits that are new in the scope.
     * @param gitRepository is the repository.
     * @param newObserveFromDate is the new observeFromDate.
     */
    public void updateObserveFromDateOfRepository(GitRepository gitRepository, LocalDate newObserveFromDate) {
        LocalDate oldObserveFromDate = gitRepository.getObserveFromDate();

        Collection<GitCommit> commits = gitTrackingAccess.getAllCommits(gitRepository.getId());

        Set<String> commitsToBenchmark = new HashSet<>();
        Set<String> commitsToRemove = new HashSet<>();
        Set<String> jobsToRemove = new HashSet<>();

        for (GitCommit commit : commits) {
            if (commit.getCommitMessage().contains(ignoreTag)) {
                continue;
            }

            String commitHash = commit.getCommitHash();
            LocalDate commitDate = commit.getCommitDate().toLocalDate();

            if (isTracked(commitDate, newObserveFromDate)) {
                if (!commitBenchmarkedChecker.isCommitBenchmarked(commitHash)) {
                    commitsToBenchmark.add(commitHash);
                }
            } else {
                if (commitBenchmarkedChecker.isCommitBenchmarked(commitHash)) {
                    commitsToRemove.add(commitHash);
                } else if (isTracked(commitDate, oldObserveFromDate)) {
                    jobsToRemove.add(commitHash);
                }
            }
        }

        LOGGER.info("Adding {} new jobs.", commitsToBenchmark.size());
        jobScheduler.addJobs(gitRepository.getPullURL(), commitsToBenchmark);

        LOGGER.info("Removing {} jobs that are out of scope.", jobsToRemove.size());
        jobScheduler.removeJobs(gitRepository.getPullURL(), jobsToRemove);

        LOGGER.info("Removing {} benchmarks that are out of scope.", commitsToRemove.size());
        resultDeleter.deleteBenchmarkingResults(commitsToRemove);

        gitRepository.setObserveFromDate(newObserveFromDate);
        gitTrackingAccess.updateRepository(gitRepository);
    }

    private boolean isTracked(LocalDate commitDate, LocalDate observeFromDate) {
        return observeFromDate == null || observeFromDate.isBefore(commitDate) || observeFromDate.isEqual(commitDate);
    }

    /**
     * @return whether the system is pulling from all repositories at the moment.
     */
    public boolean isPullingFromAllRepositories() {
        return pullingFromAllRepositories;
    }
}
