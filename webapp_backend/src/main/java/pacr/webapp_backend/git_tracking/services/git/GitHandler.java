package pacr.webapp_backend.git_tracking.services.git;

import javassist.NotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.shared.IResultDeleter;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Is responsible for handling JGit. Pulls from repositories, clones repositories.
 *
 * @author Pavel Zwerschke
 */
@Component
public class GitHandler {

    private static final Logger LOGGER = LogManager.getLogger(GitHandler.class);

    private String pathToWorkingDir;

    private IGitTrackingAccess gitTrackingAccess;
    private TransportConfigCallback transportConfigCallback;
    private ICleanUpCommits cleanUpCommits;
    private IResultDeleter resultDeleter;

    /**
     * Creates an instance of GitHandler.
     * @param pathToRepositories is the path where the repositories are being stored.
     * @param transportConfigCallback is the TransportConfigCallback needed for SSH authentication.
     * @param gitTrackingAccess is the access to the DB for storing commits.
     * @param cleanUpCommits is the strategy that is used to select the commits not being needed anymore
     *                       after a force push.
     * @param resultDeleter is the component used to delete benchmarking results.
     * @throws IOException when the working directory cannot be created.
     */
    public GitHandler(@NotNull @Value("${gitRepositoriesPath}") String pathToRepositories,
                      @NotNull TransportConfigCallback transportConfigCallback,
                      @NotNull IGitTrackingAccess gitTrackingAccess,
                      @NotNull ICleanUpCommits cleanUpCommits,
                      @NotNull IResultDeleter resultDeleter) throws IOException {
        Objects.requireNonNull(pathToRepositories);
        Objects.requireNonNull(transportConfigCallback);
        Objects.requireNonNull(gitTrackingAccess);
        Objects.requireNonNull(cleanUpCommits);
        Objects.requireNonNull(resultDeleter);

        this.transportConfigCallback = transportConfigCallback;
        this.pathToWorkingDir = System.getProperty("user.dir") + pathToRepositories;
        this.gitTrackingAccess = gitTrackingAccess;
        this.cleanUpCommits = cleanUpCommits;
        this.resultDeleter = resultDeleter;

        File repositoryWorkingDir = new File(pathToWorkingDir);

        if (!repositoryWorkingDir.exists()) {
            if (!repositoryWorkingDir.mkdirs()) {
                throw new IOException("Could not create repository working directory.");
            }
        }
    }

    /**
     * Updates a repository. Initially clones it if it doesn't exist yet or pulls the repository.
     * @param gitRepository is the Repository being updated.
     * @return new commits that need to be added or null if something went wrong.
     */
    public Collection<GitCommit> pullFromRepository(@NotNull GitRepository gitRepository) {
        Objects.requireNonNull(gitRepository);

        // clone repository if it wasn't cloned already
        File repositoryFolder = cloneRepositoryIfNotExists(gitRepository);
        if (repositoryFolder == null) {
            LOGGER.error("Could not clone repository {} ({}).", gitRepository.getName(), gitRepository.getId());
            return null;
        }

        Git git = initializeGit(repositoryFolder);
        if (git == null) {
            LOGGER.error("Could not read repository {} ({}).", gitRepository.getName(), gitRepository.getId());
            return null;
        }

        // fetch repository
        LOGGER.info("Fetching repository {} ({}).", gitRepository.getName(), gitRepository.getId());
        try {
            fetchRepository(git);
        } catch (GitAPIException e) {
            LOGGER.error("Could not fetch repository {} ({}).", gitRepository.getName(), gitRepository.getId());
        }

        // delete branches that are not in origin anymore
        deleteBranches(git, gitRepository);

        Set<GitCommit> untrackedCommits = new HashSet<>();

        // get branches
        List<Ref> branches = getBranches(git);
        if (branches == null) {
            LOGGER.error("Could not get branches from repository {} ({}).",
                    gitRepository.getName(), gitRepository.getId());
            return null;
        }

        for (Ref branch : branches) {

            try {
                checkBranch(branch, git, gitRepository, untrackedCommits);
            } catch (ForcePushException e) {
                handleForcePush(git, gitRepository, branch);

                // try again with reset history
                return pullFromRepository(gitRepository);
            }
        }

        // search for git tags
        LOGGER.info("Searching for Git-Tags.");
        searchForGitTags(git, gitRepository);
        gitTrackingAccess.updateRepository(gitRepository);

        git.getRepository().close();
        git.close();

        return untrackedCommits;
    }

    private void handleForcePush(Git git, GitRepository gitRepository, Ref branch) {
        GitBranch gitBranch = gitRepository.getSelectedBranch(getNameOfBranch(branch));

        gitBranch.setLocalHead(null);

        Collection<String> toDelete = cleanUpCommits.cleanUp(git, gitRepository);

        for (String commitHash : toDelete) {
            resultDeleter.deleteBenchmarkingResults(commitHash);

            gitRepository.removeCommit(commitHash);
            gitTrackingAccess.removeCommit(commitHash);
        }
        gitTrackingAccess.updateRepository(gitRepository);
    }

    private void searchForGitTags(Git git, GitRepository gitRepository) {
        List<Ref> tags;
        try {
            tags = git.tagList().call();
        } catch (GitAPIException e) {
            LOGGER.error("Could not get Git-Tags.");
            return;
        }

        for (Ref tag : tags) {
            String taggedHash = tag.getObjectId().getName();
            GitCommit tagged = gitRepository.getCommit(taggedHash);

            tagged.addLabel(getNameOfLabel(tag));
        }
    }

    /**
     * Deletes branches that are not in origin anymore.
     * @param git is the access to origin.
     * @param gitRepository is the git repository with the selected branches.
     */
    private void deleteBranches(Git git, GitRepository gitRepository) {

        Map<String, Boolean> branchNotDeleted = new HashMap<>();

        for (GitBranch branch : gitRepository.getSelectedBranches()) {
            branchNotDeleted.put(branch.getName(), Boolean.FALSE);
        }

        for (String branchName : getBranchNames(git)) {
            if (branchNotDeleted.containsKey(branchName)) {
                branchNotDeleted.put(branchName, Boolean.TRUE);
            }
        }

        for (Map.Entry<String, Boolean> entry : branchNotDeleted.entrySet()) {
            if (!entry.getValue()) {
                GitBranch branch = gitRepository.getSelectedBranch(entry.getKey());

                gitRepository.removeBranchFromSelection(branch);
            }
        }

    }

    private File cloneRepositoryIfNotExists(GitRepository gitRepository) {
        File repositoryFolder = getRepositoryWorkingDir(gitRepository);

        if (!repositoryFolder.exists()) {
            repositoryFolder.mkdirs();
            try {
                cloneRepository(gitRepository);
            } catch (GitAPIException e) {
                e.printStackTrace();
                return null;
            }
        }
        return repositoryFolder;
    }

    private Git initializeGit(File repositoryDir) {
        Repository repository = null;
        try {
            repository = getRepository(repositoryDir.getAbsolutePath());
        } catch (IOException e) {
            return null;
        }

        return new Git(repository);
    }

    private void fetchRepository(Git git) throws GitAPIException {
        git.fetch().setRemote("origin")
                .setTransportConfigCallback(transportConfigCallback)
                .call();
    }

    private List<Ref> getBranches(Git git) {
        List<Ref> branches = null;
        try {
            branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return branches;
    }

    private List<String> getBranchNames(Git git) {
        List<Ref> branches = getBranches(git);

        List<String> branchNames = new ArrayList<>();

        for (Ref branch : branches) {
            branchNames.add(getNameOfBranch(branch));
        }

        return branchNames;
    }

    private void checkBranch(Ref branch, Git git, GitRepository gitRepository,
                             Set<GitCommit> untrackedCommits) throws ForcePushException {

        if (gitRepository.isBranchSelected(getNameOfBranch(branch))) {
            GitBranch gitBranch = gitRepository.getSelectedBranch(getNameOfBranch(branch));

            LOGGER.info("Searching for new commits in branch {}.", getNameOfBranch(branch));
            // get commits from branch
            List<String> benchmarkedCommitsNotInBranch = new ArrayList<>();

            Set<GitCommit> commitsFromBranch = searchForNewCommitsInBranch(git, gitRepository,
                        branch, benchmarkedCommitsNotInBranch);

            untrackedCommits.addAll(commitsFromBranch);

            // commitsFromBranch were already added to the repository in searchForNewCommits
            gitTrackingAccess.updateRepository(gitRepository);

            LOGGER.info("Added {} commits to the system for branch {}.",
                    commitsFromBranch.size(), getNameOfBranch(branch));

            // check if branch contains commits that are already added to the system
            if (!benchmarkedCommitsNotInBranch.isEmpty()) {

                for (String commitHash : benchmarkedCommitsNotInBranch) {
                    // get commits from ram so that there are fewer DB calls and untrackedCommits are up to date
                    GitCommit commit = getCommitFromSet(commitHash, untrackedCommits);
                    if (commit == null) { // not found in untrackedCommits
                        commit = gitRepository.getCommit(commitHash);
                    }
                    assert commit != null;

                    commit.addBranch(gitBranch);
                }

                gitTrackingAccess.updateRepository(gitRepository);
            }

            // update branch head
            setBranchHead(branch, gitRepository, gitBranch);
        } else {
            LOGGER.info("Skipping branch {} because it is not selected.", getNameOfBranch(branch));
        }
    }

    private void setBranchHead(Ref branch, GitRepository gitRepository, GitBranch gitBranch) {

        GitCommit head = gitRepository.getCommit(branch.getObjectId().getName());

        gitBranch.setLocalHead(head);
    }

    private GitCommit getCommitFromSet(String commitHash, Set<GitCommit> commits) {
        for (GitCommit commit : commits) {
            if (commitHash.equals(commit.getCommitHash())) {
                return commit;
            }
        }
        return null;
    }

    private GitCommit createCommit(GitRepository gitRepository, RevCommit commit) {
        String commitHash = commit.getName();
        String commitMessage = commit.getShortMessage();

        PersonIdent authorIdent = commit.getAuthorIdent();
        Date authorDateDate = authorIdent.getWhen();
        LocalDateTime authorDate = LocalDateTime.ofInstant(authorDateDate.toInstant(), ZoneId.systemDefault());

        long commitTime = commit.getCommitTime();
        LocalDateTime commitDate = LocalDateTime.ofEpochSecond(commitTime, 0,
                ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));

        GitCommit gitCommit = new GitCommit(commitHash, commitMessage, commitDate, authorDate, gitRepository);

        for (int i = 0; i < commit.getParentCount(); ++i) {
            gitCommit.addParent(commit.getParent(i).getName());
        }

        return gitCommit;
    }

    private Set<GitCommit> searchForNewCommitsInBranch(Git git, GitRepository gitRepository, Ref branch,
                                                              List<String> benchmarkedCommitsNotInBranch)
            throws ForcePushException {

        assert git != null;
        assert gitRepository != null;
        assert branch != null;
        assert benchmarkedCommitsNotInBranch != null;

        String branchName = getNameOfBranch(branch);
        GitBranch gitBranch = gitRepository.getSelectedBranch(branchName);

        // iterate over all commits from branch
        Iterable<RevCommit> commitsIterable = null;
        try {
            commitsIterable = git.log().add(branch.getObjectId()).call();
        } catch (MissingObjectException | IncorrectObjectTypeException | GitAPIException e) {
            LOGGER.error("Could not get commits from branch {}", branchName);
            return new HashSet<>();
        }

        // add all new commits to commits ordered by their commit history
        // and check that no force push occurred
        List<RevCommit> commits = getNewCommits(commitsIterable, benchmarkedCommitsNotInBranch, gitBranch);
        List<GitCommit> commitsToAdd = new ArrayList<>();

        for (RevCommit revCommit : commits) {
            if (revCommit.getFullMessage().contains("#pacr-ignore")) {
                continue;
            }

            GitCommit commit = createCommit(gitRepository, revCommit);

            if (revCommit.getFullMessage().contains("#pacr-label")) {
                commit.addLabel(searchForLabel(revCommit.getFullMessage()));
            }

            commit.addBranch(gitBranch);

            commitsToAdd.add(commit);
        }

        return new HashSet<>(commitsToAdd);
    }

    private String searchForLabel(String message) {
        int startIndex = message.indexOf("#pacr-label");
        int endIndex = message.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = message.length();
        }

        return message.substring(startIndex, endIndex);
    }

    /**
     * Looks for new commits that are not added to the system yet.
     * If a commit which is already in the system but does not belong to the branch yet,
     * it is added to benchmarkedCommitsNotInBranch.
     * @param commitsIterable is the output of git log.
     * @param branch is the branch currently looked at.
     * @param benchmarkedCommitsNotInBranch is the list of commits that are already in the system,
     *                                      but not in the branch yet.
     * @return list of new commits.
     * @throws ForcePushException if a force push was detected.
     */
    private List<RevCommit> getNewCommits(Iterable<RevCommit> commitsIterable,
                                          List<String> benchmarkedCommitsNotInBranch, GitBranch branch)
            throws ForcePushException {
        List<RevCommit> commits = new ArrayList<>();

        for (RevCommit commit : commitsIterable) {
            if (gitTrackingAccess.containsCommit(commit.getName())) {

                GitCommit localHead = branch.getLocalHead();
                GitCommit alreadyContained = gitTrackingAccess.getCommit(commit.getName());

                if (localHead == null || !alreadyContained.getBranchNames().contains(branch.getName())) {
                    // branch doesn't have the commit yet
                    benchmarkedCommitsNotInBranch.add(commit.getName());
                } else if (!localHead.getCommitHash().equals(commit.getName())) { // check for force push
                    LOGGER.info("LOCALHEAD CH: {}", localHead.getCommitHash());
                    LOGGER.info("COMMIT HASH {}", commit.getName());
                    LOGGER.info("Force push detected at {}. Deleting unused commits.", commit.getName());
                    throw new ForcePushException();
                } else { // all new commits from branch found
                    break;
                }
            } else {
                commits.add(commit);
            }
        }

        return commits;
    }

    private Repository getRepository(String path) throws IOException {
        assert path != null;

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
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
    public void cloneRepository(@NotNull GitRepository gitRepository) throws GitAPIException {
        Objects.requireNonNull(gitRepository);

        LOGGER.info("Cloning repository {} ({}). URL: {}.", gitRepository.getName(), gitRepository.getId(),
                gitRepository.getPullURL());

        Git git = Git.cloneRepository()
                .setDirectory(getRepositoryWorkingDir(gitRepository))
                .setTransportConfigCallback(transportConfigCallback)
                .setURI(gitRepository.getPullURL())
                .call();

        git.getRepository().close();
        git.close();
    }

    private File getRepositoryWorkingDir(GitRepository repository) {
        assert repository != null;
        String repositoryFolderPath = pathToWorkingDir + "/" + repository.getId();
        return new File(repositoryFolderPath);
    }

    /**
     * Returns the name of a branch.
     * @param branch is the branch of which the name should be returned.
     * @return branch name
     */
    public static String getNameOfBranch(@NotNull Ref branch) {
        Objects.requireNonNull(branch);

        // remove the "refs/remotes/origin/" part
        return branch.getName().substring(20);
    }

    private String getNameOfLabel(Ref tag) {
        // returns the "refs/tags/" part
        return tag.getName().substring(10);
    }

}
