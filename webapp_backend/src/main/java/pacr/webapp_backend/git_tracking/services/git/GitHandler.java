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
import pacr.webapp_backend.git_tracking.services.ColorPicker;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.shared.IResultDeleter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

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
    public Collection<GitCommit> updateRepository(@NotNull GitRepository gitRepository) {
        Objects.requireNonNull(gitRepository);

        // clone repository if it wasn't cloned already
        File repositoryFolder = cloneRepositoryIfNotExists(gitRepository);
        if (repositoryFolder == null) {
            LOGGER.error("Could not clone repository with ID {}.", gitRepository.getId());
            return null;
        }

        Git git = initializeGit(repositoryFolder);
        if (git == null) {
            LOGGER.error("Could not read repository with ID {}.", gitRepository.getId());
            return null;
        }

        // fetch repository
        LOGGER.info("Fetching repository with ID {}.", gitRepository.getId());
        try {
            fetchRepository(git);
        } catch (GitAPIException e) {
            LOGGER.error("Could not fetch repository with ID {}.", gitRepository.getId());
        }

        Set<GitCommit> untrackedCommits = new HashSet<>();

        // get branches
        List<Ref> branches = getBranches(git);
        if (branches == null) {
            LOGGER.error("Could not get branches from repository with ID {}.", gitRepository.getId());
            return null;
        }

        for (Ref branch : branches) {

            try {
                checkBranch(branch, git, gitRepository, untrackedCommits);
            } catch (ForcePushException e) {
                LOGGER.info("Force push detected. Deleting unused commits.");
                Collection<String> toDelete = cleanUpCommits.cleanUp(git, gitRepository);

                for (String commitHash : toDelete) {
                    resultDeleter.deleteBenchmarkingResults(commitHash);
                    gitTrackingAccess.removeCommit(commitHash);
                }
                // try again with reset history
                return updateRepository(gitRepository);
            }

        }

        git.getRepository().close();
        git.close();

        return untrackedCommits;
    }

    private File cloneRepositoryIfNotExists(GitRepository gitRepository) {
        File repositoryFolder = getRepositoryWorkingDir(gitRepository);

        if (!repositoryFolder.exists()) {
            boolean successful = repositoryFolder.mkdirs();
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

    private void checkBranch(Ref branch, Git git, GitRepository gitRepository,
                             Set<GitCommit> untrackedCommits) throws ForcePushException {

        if (gitRepository.isBranchSelected(getNameOfBranch(branch))) {
            LOGGER.info("Searching for new commits in branch {}.", getNameOfBranch(branch));
            // get commits from branch
            Set<String> benchmarkedCommitsNotInBranch = new HashSet<>();

            Set<GitCommit> commitsFromBranch = searchForNewCommitsInBranch(git, gitRepository,
                        branch, benchmarkedCommitsNotInBranch);

            untrackedCommits.addAll(commitsFromBranch);

            try {
                // commitsFromBranch were already added to the repository in searchForNewCommits
                gitTrackingAccess.updateRepository(gitRepository);
            } catch (NotFoundException e) {
                throw new RuntimeException("Repository should be found.");
            }

            LOGGER.info("Added {} commits to the system for branch {}.",
                    commitsFromBranch.size(), getNameOfBranch(branch));

            // check if branch contains commits that are already added to the system
            if (!benchmarkedCommitsNotInBranch.isEmpty()) {

                for (String commitHash : benchmarkedCommitsNotInBranch) {
                    GitCommit commit = gitTrackingAccess.getCommit(commitHash);
                    GitBranch gitBranch;
                    try {
                        gitBranch = gitRepository.getSelectedBranch(getNameOfBranch(branch));
                    } catch (NotFoundException e) {
                        LOGGER.error("Could not find branch {}.", getNameOfBranch(branch));
                        return;
                    }
                    commit.addBranch(gitBranch);
                }

                try {
                    gitTrackingAccess.updateRepository(gitRepository);
                } catch (NotFoundException e) {
                    throw new RuntimeException("Repository should be found.");
                }
            }
        } else {
            LOGGER.info("Skipping branch {} because it is not selected.", getNameOfBranch(branch));
        }
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
                                                              Set<String> benchmarkedCommitsNotInBranch)
            throws ForcePushException {

        assert git != null;
        assert gitRepository != null;
        assert branch != null;
        assert benchmarkedCommitsNotInBranch != null;

        String branchName = getNameOfBranch(branch);
        GitBranch gitBranch;
        try {
            gitBranch = gitRepository.getSelectedBranch(branchName);
        } catch (NotFoundException e) {
            throw new RuntimeException(); //todo
        }

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
            GitCommit commit = createCommit(gitRepository, revCommit);
            commit.addBranch(gitBranch);

            String commitHash = commit.getCommitHash();

            commitsToAdd.add(commit);
        }

        if (!commitsToAdd.isEmpty()) {
            gitBranch.setLocalHead(commitsToAdd.get(commitsToAdd.size() - 1));
        }

        return new HashSet<>(commitsToAdd);
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
                                          Collection<String> benchmarkedCommitsNotInBranch, GitBranch branch)
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

        LOGGER.info("Cloning repository with id {} and URL {}.", gitRepository.getId(), gitRepository.getPullURL());

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

}
