package pacr.webapp_backend.git_tracking.services.git;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.IResultDeleter;

import javax.validation.constraints.NotNull;

/**
 * Is responsible for handling JGit. Pulls from repositories, clones repositories.
 *
 * @author Pavel Zwerschke
 */
@Component
public class GitHandler {

    private static final Logger LOGGER = LogManager.getLogger(GitHandler.class);

    private static final int FIRST_CATCH_GROUP = 1;
    private Pattern pattern;

    private String pathToWorkingDir;
    private String ignoreTag;

    private final IGitTrackingAccess gitTrackingAccess;
    private final TransportConfigCallback transportConfigCallback;
    private final ICleanUpCommits cleanUpCommits;
    private final IResultDeleter resultDeleter;

    /**
     * Creates an instance of GitHandler.
     * @param pathToRepositories is the path where the repositories are being stored.
     * @param transportConfigCallback is the TransportConfigCallback needed for SSH authentication.
     * @param gitTrackingAccess is the access to the DB for storing commits.
     * @param cleanUpCommits is the strategy that is used to select the commits not being needed anymore
     *                       after a force push.
     * @param resultDeleter is the component used to delete benchmarking results.
     * @param ignoreTag is the ignore tag.
     * @param labelTag is the label tag.
     * @throws IOException when the working directory cannot be created.
     */
    public GitHandler(@NotNull @Value("${gitRepositoriesPath}") String pathToRepositories,
                      @NotNull TransportConfigCallback transportConfigCallback,
                      @NotNull IGitTrackingAccess gitTrackingAccess,
                      @NotNull ICleanUpCommits cleanUpCommits,
                      @NotNull IResultDeleter resultDeleter,
                      @NotNull @Value("${ignoreTag}") String ignoreTag,
                      @NotNull @Value("${labelTag}") String labelTag) throws IOException {
        Objects.requireNonNull(pathToRepositories);
        Objects.requireNonNull(transportConfigCallback);
        Objects.requireNonNull(gitTrackingAccess);
        Objects.requireNonNull(cleanUpCommits);
        Objects.requireNonNull(resultDeleter);
        Objects.requireNonNull(ignoreTag);
        Objects.requireNonNull(labelTag);

        this.transportConfigCallback = transportConfigCallback;
        this.pathToWorkingDir = System.getProperty("user.dir") + pathToRepositories;
        this.gitTrackingAccess = gitTrackingAccess;
        this.cleanUpCommits = cleanUpCommits;
        this.resultDeleter = resultDeleter;
        this.ignoreTag = ignoreTag;

        String labelTagRegex = ".*" + labelTag + "\\((.*)\\).*";
        this.pattern = Pattern.compile(labelTagRegex);

        File repositoryWorkingDir = new File(pathToWorkingDir);

        if (!repositoryWorkingDir.exists() && !repositoryWorkingDir.mkdirs()) {
            throw new IOException("Could not create repository working directory.");
        }
    }

    /**
     * Updates a repository. Initially clones it if it doesn't exist yet or pulls the repository.
     * This method needs to be called after the repository got updated an has all available tracked branches.
     * @param gitRepository is the Repository being updated.
     * @return new commits that need to be added.
     * @throws PullFromRepositoryException if something went wrong.
     */
    public Set<String> pullFromRepository(@NotNull GitRepository gitRepository) throws PullFromRepositoryException {
        Objects.requireNonNull(gitRepository);

        Git git = initialize(gitRepository);
        if (git == null) {
            throw new PullFromRepositoryException("Could not initialize Git.");
        }

        // delete branches that are not in origin anymore
        deleteBranches(git, gitRepository);

        // get branches
        final List<Ref> branches = getBranches(git);
        if (branches == null) {
            LOGGER.error("Could not get branches from repository {} ({}).",
                    gitRepository.getName(), gitRepository.getId());
            throw new PullFromRepositoryException("Could not get branches.");
        }

        Set<String> commitsToBenchmark = getCommitsToBenchmark(git, gitRepository, branches);

        searchForGitTags(git);

        git.getRepository().close();
        git.close();

        return commitsToBenchmark;
    }

    private Set<String> getCommitsToBenchmark(Git git, GitRepository gitRepository, List<Ref> branches)
            throws PullFromRepositoryException {
        Set<String> commitsToBenchmark = new HashSet<>();

        Set<Ref> branchesWithForcePush = new HashSet<>();

        Set<String> activeBranches = getBranchesOfRepository(gitRepository.getPullURL());

        for (Ref branch : branches) {
            String name = getNameOfRef(branch);
            if (activeBranches.contains(name) && gitRepository.isBranchSelected(name)) {
                try {
                    commitsToBenchmark.addAll(collectCommitsToBenchmark(branch, git, gitRepository));
                } catch (ForcePushException e) {
                    handleForcePush(git, gitRepository, branch);
                    branchesWithForcePush.add(branch);
                }
            }
        }

        for (Ref branch : branchesWithForcePush) {
            try {
                commitsToBenchmark.addAll(collectCommitsToBenchmark(branch, git, gitRepository));
            } catch (ForcePushException e) {
                throw new PullFromRepositoryException("Couldn't resolve force push.");
            }
        }

        gitTrackingAccess.updateRepository(gitRepository);

        return commitsToBenchmark;
    }

    private Set<String> collectCommitsToBenchmark(Ref branch, Git git, GitRepository gitRepository)
            throws ForcePushException {
        Set<String> commitsFromBranch = checkBranch(branch, git, gitRepository);
        updateBranchHead(branch, gitRepository);

        return commitsFromBranch;
    }

    private void updateBranchHead(Ref branch, GitRepository gitRepository) {
        GitBranch gitBranch = gitRepository.getTrackedBranch(getNameOfRef(branch));
        setBranchHead(branch, gitBranch);
    }

    private Git initialize(GitRepository gitRepository) {
        // clone repository if it wasn't cloned already
        File repositoryFolder = cloneRepositoryIfNotExists(gitRepository);
        if (repositoryFolder == null) {
            return null;
        }

        Git git = initializeGit(repositoryFolder);
        if (git == null) {
            return null;
        }

        // fetch repository
        LOGGER.info("Fetching repository {} ({}).", gitRepository.getName(), gitRepository.getId());
        try {
            fetchRepository(git);
        } catch (GitAPIException e) {
            LOGGER.error("Could not fetch repository {} ({}).", gitRepository.getName(), gitRepository.getId());
        }

        return git;
    }

    private void handleForcePush(Git git, GitRepository gitRepository, Ref branch) {
        Set<String> toDelete = cleanUpCommits.cleanUp(git, gitRepository, gitTrackingAccess);

        resultDeleter.deleteBenchmarkingResults(toDelete);
        gitTrackingAccess.removeCommits(toDelete);

        gitTrackingAccess.updateRepository(gitRepository);
    }

    private void searchForGitTags(Git git) {
        LOGGER.info("Searching for Git-Tags.");

        List<Ref> tags;
        try {
            tags = git.tagList().call();
        } catch (final GitAPIException e) {
            LOGGER.error("Could not get Git-Tags.");
            return;
        }

        for (final Ref tag : tags) {
            final String taggedHash = tag.getObjectId().getName();

            final GitCommit tagged = gitTrackingAccess.getCommit(taggedHash);

            if (tagged != null) {
                tagged.addLabel(getNameOfRef(tag));
                gitTrackingAccess.updateCommit(tagged);
            }
        }
    }

    /**
     * Deletes branches that are not in origin anymore.
     * @param git is the access to origin.
     * @param gitRepository is the git repository with the selected and tracked branches.
     */
    private void deleteBranches(final Git git, final GitRepository gitRepository) {
        // garbage collection for trackedBranches
        Map<String, Boolean> branchNotDeleted = new HashMap<>();
        Set<String> branchesOfRepository = getBranchesOfRepository(gitRepository.getPullURL());

        for (final GitBranch branch : gitRepository.getTrackedBranches()) {
            branchNotDeleted.put(branch.getName(), Boolean.FALSE);
        }

        for (String branchName : branchesOfRepository) {
            if (branchNotDeleted.containsKey(branchName)) {
                branchNotDeleted.put(branchName, Boolean.TRUE);
            }
        }

        for (final Map.Entry<String, Boolean> entry : branchNotDeleted.entrySet()) {
            if (!entry.getValue()) {
                final GitBranch branch = gitRepository.getTrackedBranch(entry.getKey());

                gitRepository.removeBranchFromSelection(branch);
            }
        }

        // garbage collection for selectedBranches
        branchNotDeleted = new HashMap<>();
        final Set<String> selectedBranches = gitRepository.getSelectedBranches();

        for (final String branch : selectedBranches) {
            branchNotDeleted.put(branch, Boolean.FALSE);
        }

        for (String branchName : branchesOfRepository) {
            if (branchNotDeleted.containsKey(branchName)) {
                branchNotDeleted.put(branchName, Boolean.TRUE);
            }
        }

        for (final Map.Entry<String, Boolean> entry : branchNotDeleted.entrySet()) {
            if (!entry.getValue()) {
                selectedBranches.remove(entry.getKey());
            }
        }

    }

    private File cloneRepositoryIfNotExists(final GitRepository gitRepository) {
        final File repositoryFolder = getRepositoryWorkingDir(gitRepository);

        if (!repositoryFolder.exists()) {
            repositoryFolder.mkdirs();
            try {
                cloneRepository(gitRepository);
            } catch (GitAPIException e) {
                LOGGER.error("Could not clone repository {} ({}).", gitRepository.getName(), gitRepository.getId());
                return null;
            }
        }
        return repositoryFolder;
    }

    private Git initializeGit(final File repositoryDir) {
        final Repository repository;
        try {
            repository = getRepository(repositoryDir.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }

        return new Git(repository);
    }

    private void fetchRepository(final Git git) throws GitAPIException {
        git.fetch().setRemote("origin")
                .setTransportConfigCallback(transportConfigCallback)
                .call();
    }

    private List<Ref> getBranches(final Git git) {
        List<Ref> branches = null;
        try {
            branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        } catch (GitAPIException e) {
            LOGGER.error(e);
        }
        return branches;
    }

    private Set<String> checkBranch(Ref branch, Git git, GitRepository gitRepository) throws ForcePushException {
        Set<String> newCommits = new HashSet<>();

        LOGGER.info("Searching for new commits in branch {}.", getNameOfRef(branch));

        Set<GitCommit> commitsFromBranch = searchForNewCommitsInBranch(git, gitRepository, branch);

        LOGGER.info("Adding {} commits to database for branch {}.",
                commitsFromBranch.size(), getNameOfRef(branch));

        gitTrackingAccess.addCommits(commitsFromBranch);

        LocalDate observeFromDate = gitRepository.getObserveFromDate();
        for (GitCommit commit : commitsFromBranch) {
            LocalDate commitDate = commit.getCommitDate().toLocalDate();

            // only add commits that are after observeFromDate
            if (observeFromDate == null || isTracked(commitDate, observeFromDate)) {
                if (!commit.getCommitMessage().contains(ignoreTag)) {
                    newCommits.add(commit.getCommitHash());
                }
            }
        }

        return newCommits;
    }

    private boolean isTracked(LocalDate commitDate, LocalDate observeFromDate) {
        return observeFromDate.isBefore(commitDate) || observeFromDate.isEqual(commitDate);
    }

    private void setBranchHead(Ref branch, GitBranch gitBranch) {

        final String headHash = branch.getObjectId().getName();

        LOGGER.info("Setting head {} for branch {}.", headHash, gitBranch.getName());

        gitBranch.setHeadHash(headHash);
    }

    private GitCommit createCommit(final GitRepository gitRepository, final RevCommit commit) {
        final String commitHash = commit.getName();
        final String commitMessage = commit.getShortMessage();

        final PersonIdent authorIdent = commit.getAuthorIdent();
        final Date authorDateDate = authorIdent.getWhen();
        final LocalDateTime authorDate = LocalDateTime.ofInstant(authorDateDate.toInstant(), ZoneId.systemDefault());

        final long commitTime = commit.getCommitTime();
        final LocalDateTime commitDate = LocalDateTime.ofEpochSecond(commitTime, 0,
                ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));

        final GitCommit gitCommit = new GitCommit(commitHash, commitMessage, commitDate, authorDate, gitRepository);

        for (int i = 0; i < commit.getParentCount(); ++i) {
            gitCommit.addParent(commit.getParent(i).getName());
        }

        return gitCommit;
    }

    private Set<GitCommit> searchForNewCommitsInBranch(final Git git, final GitRepository gitRepository, final Ref branch)
            throws ForcePushException {

        assert git != null;
        assert gitRepository != null;
        assert branch != null;

        final String branchName = getNameOfRef(branch);

        // iterate over all commits from branch
        final Iterable<RevCommit> commitsIterable;
        try {
            commitsIterable = git.log().add(branch.getObjectId()).call();
        } catch (final MissingObjectException | IncorrectObjectTypeException | GitAPIException e) {
            LOGGER.error("Could not get commits from branch {}", branchName);
            return new HashSet<>();
        }

        final GitBranch gitBranch = gitRepository.getTrackedBranch(branchName);

        // add all new commits to commits ordered by their commit history
        List<RevCommit> commits = getNewCommits(commitsIterable, gitBranch);
        List<GitCommit> commitsToAdd = new ArrayList<>();

        for (RevCommit revCommit : commits) {
            GitCommit commit = createCommit(gitRepository, revCommit);

            Matcher m = pattern.matcher(revCommit.getFullMessage());
            if (m.matches()) {
                commit.addLabel(m.group(FIRST_CATCH_GROUP));
            }

            commit.addBranch(gitBranch);

            commitsToAdd.add(commit);
        }

        return new HashSet<>(commitsToAdd);
    }

    /**
     * Looks for new commits that are not added to the system yet.
     * If a commit which is already in the system but does not belong to the branch yet,
     * it is added to benchmarkedCommitsNotInBranch.
     * @param commitsIterable is the output of git log.
     * @param branch is the branch currently looked at.
     * @return list of new commits.
     * @throws ForcePushException if a force push was detected.
     */
    private List<RevCommit> getNewCommits(final Iterable<RevCommit> commitsIterable, final GitBranch branch)
            throws ForcePushException {
        List<RevCommit> commits;

        Set<String> hashesToUpdate = new HashSet<>();

        if (branch.getHeadHash() == null) { // branch not initialized
            commits = getNewCommitsForUnbornBranch(commitsIterable, branch, hashesToUpdate);
        } else {
            commits = getNewCommitsForExistingBranch(commitsIterable, branch, hashesToUpdate);
        }

        updateCommits(hashesToUpdate, branch);

        return commits;
    }

    private void updateCommits(Set<String> hashesToUpdate, GitBranch branch) {
        Set<GitCommit> commitsToUpdate = gitTrackingAccess.getCommits(hashesToUpdate);

        for (GitCommit commit : commitsToUpdate) {
            commit.addBranch(branch);
        }

        gitTrackingAccess.updateCommits(commitsToUpdate);
    }

    private List<RevCommit> getNewCommitsForExistingBranch(Iterable<RevCommit> commitsIterable, GitBranch branch,
                                                           Set<String> hashesToUpdate) throws ForcePushException {
        List<RevCommit> commits = new ArrayList<>();
        String headHash = branch.getHeadHash();

        for (RevCommit commit : commitsIterable) {

            if (gitTrackingAccess.containsCommit(commit.getName())) {

                GitCommit alreadyContained = gitTrackingAccess.getCommit(commit.getName());

                if (!alreadyContained.isOnBranch(branch.getName())) {
                    alreadyContained.addBranch(branch);
                    hashesToUpdate.add(commit.getName());
                } else if (!headHash.equals(commit.getName())) { // check for force push
                    branch.setHeadHash(commit.getName());
                    LOGGER.info("Force push detected at {}.", commit.getName());
                    throw new ForcePushException();
                } else { // all new commits from branch found
                    LOGGER.info("DB contains {}: breaking", commit.getName());
                    break;
                }
            } else {
                commits.add(commit);
            }
        }

        return commits;
    }

    private List<RevCommit> getNewCommitsForUnbornBranch(Iterable<RevCommit> commitIterable, GitBranch branch,
                                                         Set<String> hashesToUpdate) {
        List<RevCommit> commits = new ArrayList<>();

        Iterator<RevCommit> iterator = commitIterable.iterator();

        while (iterator.hasNext()) {
            RevCommit commit = iterator.next();
            if (!gitTrackingAccess.containsCommit(commit.getName())) {
                commits.add(commit);
            } else {
                hashesToUpdate.add(commit.getName());
                break;
            }
        }

        while (iterator.hasNext()) {
            hashesToUpdate.add(iterator.next().getName());
        }

        return commits;
    }

    private Repository getRepository(final String path) throws IOException {
        assert path != null;

        final FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        return repositoryBuilder.setGitDir(
                new File(path + "/.git"))
                .readEnvironment()
                .findGitDir()
                .setMustExist(true)
                .build();
    }

    /**
     * Clones a repository to the current working directory.
     * @param gitRepository is the repository being cloned.
     * @throws GitAPIException if the Git authentication fails.
     */
    public void cloneRepository(@NotNull final GitRepository gitRepository) throws GitAPIException {
        Objects.requireNonNull(gitRepository);

        LOGGER.info("Cloning repository {} ({}). URL: {}. This may take a while.",
                gitRepository.getName(), gitRepository.getId(),
                gitRepository.getPullURL());

        final Git git = Git.cloneRepository()
                .setDirectory(getRepositoryWorkingDir(gitRepository))
                .setTransportConfigCallback(transportConfigCallback)
                .setURI(gitRepository.getPullURL())
                .call();

        git.getRepository().close();
        git.close();
    }

    private File getRepositoryWorkingDir(final GitRepository repository) {
        assert repository != null;
        final String repositoryFolderPath = pathToWorkingDir + "/" + repository.getPullURL().hashCode();
        return new File(repositoryFolderPath);
    }

    /**
     * Returns the name of a branch.
     * @param branch is the branch of which the name should be returned.
     * @return branch name
     */
    public static String getNameOfRef(@NotNull final Ref branch) {
        Objects.requireNonNull(branch);

        // remove the "refs/remotes/origin/" part
        final String branchName = branch.getName();
        final int lastIndexOfSlash = branchName.lastIndexOf('/');
        return branch.getName().substring(lastIndexOfSlash + 1);
    }

    /**
     * @param pullURL the pull-url of a repository.
     * @return a set of branches in the given repository.
     */
    public Set<String> getBranchesOfRepository(final String pullURL) {
        final Collection<Ref> refs;
        final Set<String> branches = new HashSet<>();

        try {
            refs = Git.lsRemoteRepository()
                    .setHeads(true)
                    .setRemote(pullURL)
                    .setTransportConfigCallback(transportConfigCallback)
                    .call();

            for (final Ref ref : refs) {
                branches.add(getNameOfRef(ref));
            }
        } catch (final InvalidRemoteException e) {
            LOGGER.error("InvalidRemoteException occurred in getBranchesOfRepository", e);
        } catch (final TransportException e) {
            LOGGER.error("TransportException occurred in getBranchesOfRepository", e);
        } catch (final GitAPIException e) {
            LOGGER.error("GitAPIException occurred in getBranchesOfRepository", e);
        }

        return branches;
    }

    /**
     * Starts tracking all branches from the given repository.
     * @param gitRepository the repository.
     */
    public void setBranchesToRepo(final GitRepository gitRepository) {
        final Set<String> branchNames = getBranchesOfRepository(gitRepository.getPullURL());
        for (final String branchName : branchNames) {
            if (gitRepository.isBranchSelected(branchName)) {
                gitRepository.createBranchIfNotExists(branchName);
            }
        }
    }
}
