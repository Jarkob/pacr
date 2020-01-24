package pacr.webapp_backend.git_tracking.services.git;

import javassist.NotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
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
    private static final String MASTER = "master";

    private String pathToWorkingDir;
    private File repositoryWorkingDir;

    private IGitTrackingAccess gitTrackingAccess;
    private TransportConfigCallback transportConfigCallback;
    private ICleanUpCommits cleanUpCommits;
    private IResultDeleter resultDeleter;

    /**
     * Creates an instance of GitHandler.
     * @param pathToRepositories is the path where the repositories are being stored.
     * @param transportConfigCallback is the TransportConfigCallback needed for SSH authentication.
     * @param gitTrackingAccess is the access to the DB for storing commits.
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

        this.transportConfigCallback = transportConfigCallback;
        this.pathToWorkingDir = System.getProperty("user.dir") + pathToRepositories;
        this.gitTrackingAccess = gitTrackingAccess;
        this.cleanUpCommits = cleanUpCommits;
        this.resultDeleter = resultDeleter;

        repositoryWorkingDir = new File(pathToWorkingDir);
        if (!repositoryWorkingDir.exists()) {
            if (!repositoryWorkingDir.mkdirs()) {
                throw new IOException("Could not create repository working directory.");
            }
        }
    }

    /**
     * Updates a repository. Initially clones it if it doesn't exist yet or pulls the repository.
     * @param gitRepository is the Repository being updated.
     * @return new commits that need to be added
     */
    public Collection<GitCommit> updateRepository(@NotNull GitRepository gitRepository) throws GitAPIException, IOException {
        Objects.requireNonNull(gitRepository);

        File repositoryFolder = getRepositoryWorkingDir(gitRepository);

        // clone repository if it wasn't cloned already
        if (!repositoryFolder.exists()) {
            boolean successful = repositoryFolder.mkdirs();
            cloneRepository(gitRepository);
        }

        Repository repository = null;
        try {
            repository = getRepository(repositoryFolder.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Could not read repository with ID {}.", gitRepository.getId());
            throw e;
        }

        Git git = new Git(repository);

        // fetch repository
        LOGGER.info("Fetching repository with ID {}.", gitRepository.getId());
        git.fetch().setRemote("origin")
                .setTransportConfigCallback(transportConfigCallback)
                .call();

        Map<String, GitCommit> untrackedCommits = new HashMap<>();
        Map<String, Collection<String>> parentsOfCommits = new HashMap<>();
        Map<String, Collection<String>> branchesOfCommits = new HashMap<>();
        // get branches
        List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        for (Ref branch : branches) {

            if (gitRepository.isBranchSelected(getNameOfBranch(branch))) {
                LOGGER.info("Searching for new commits in branch {}.", getNameOfBranch(branch));
                // get commits from branch
                try {
                    Collection<String> benchmarkedCommitsNotInBranch = new HashSet<>();
                    Collection<GitCommit> commitsFromBranch = searchForNewCommitsInBranch(git, gitRepository,
                            branch, parentsOfCommits, branchesOfCommits, benchmarkedCommitsNotInBranch);

                    // check if branch is being added for the first time and you need to add commits to branch
                    if (!benchmarkedCommitsNotInBranch.isEmpty()) {
                        for (String commitHash : benchmarkedCommitsNotInBranch) {
                            GitCommit commit = gitTrackingAccess.getCommit(commitHash);
                            GitBranch gitBranch = gitRepository.getSelectedBranch(getNameOfBranch(branch));
                            gitBranch.addCommit(commit);
                        }
                        gitTrackingAccess.updateRepository(gitRepository);
                    }

                    for (GitCommit commit : commitsFromBranch) {
                        untrackedCommits.put(commit.getCommitHash(), commit);
                    }
                } catch (ForcePushException e) {
                    LOGGER.info("Force push detected. Deleting unused commits.");
                    Collection<String> toDelete = cleanUpCommits.cleanUp(git, gitRepository);

                    for (String commitHash : toDelete) {
                        resultDeleter.deleteBenchmarkingResults(commitHash);
                        gitTrackingAccess.removeCommit(commitHash);
                    }
                    // try again with reset history
                    updateRepository(gitRepository);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                    // todo error handling
                }
            } else {
                LOGGER.info("Skipping branch {} because it is not selected.", getNameOfBranch(branch));
            }
        }

        git.getRepository().close();
        git.close();

        // add parents to GitCommits
        for (GitCommit commit : untrackedCommits.values()) {
            Collection<String> parentHashes = parentsOfCommits.get(commit.getCommitHash());
            assert parentHashes != null;

            for (String parentHash : parentHashes) {
                if (untrackedCommits.containsKey(parentHash)) { // parent is also untracked
                    GitCommit parent = untrackedCommits.get(parentHash);
                    commit.addParent(parent);
                } else if (gitTrackingAccess.containsCommit(parentHash)) { // parent is already tracked
                    GitCommit parent = gitTrackingAccess.getCommit(parentHash);
                    commit.addParent(parent);
                } // else parent is not in selectedBranches
            }
        }

        // add branches to GitCommits
        for (GitCommit commit : untrackedCommits.values()) {
            Collection<String> branchesNames = branchesOfCommits.get(commit.getCommitHash());
            assert branchesNames != null;

            for (String branchName : branchesNames) {
                try {
                    commit.addBranch(gitRepository.getSelectedBranch(branchName));
                } catch (NotFoundException e) {
                    LOGGER.error("Branch {} was not found in the repository.", branchName);
                }
            }
        }

        return untrackedCommits.values();
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

        return new GitCommit(commitHash, commitMessage, commitDate, authorDate, gitRepository);
    }

    private Collection<GitCommit> searchForNewCommitsInBranch(Git git, GitRepository gitRepository, Ref branch,
                                                              Map<String, Collection<String>> parents,
                                                              Map<String, Collection<String>> branches,
                                                              Collection<String> benchmarkedCommitsNotInBranch)
            throws ForcePushException {

        // iterate over all commits from branch
        Iterable<RevCommit> commits = null;
        try {
            commits = git.log().add(branch.getObjectId()).call();
        } catch (MissingObjectException | IncorrectObjectTypeException | GitAPIException e) {
            LOGGER.error("Could not get commits from branch {}", getNameOfBranch(branch));
        }

        Collection<GitCommit> untrackedCommits = new HashSet<>();
        // search for first commit that is already stored in DB
        for (RevCommit commit : commits) {
            if (!gitTrackingAccess.containsCommit(commit.getName())) {
                untrackedCommits.add(createCommit(gitRepository, commit));
                // add branch
                Collection<String> branchesOfCommit = branches.get(commit.getName());
                if (branchesOfCommit == null) {
                    branchesOfCommit = new HashSet<>();
                    branchesOfCommit.add(getNameOfBranch(branch));
                    branches.put(commit.getName(), branchesOfCommit);
                } else {
                    branchesOfCommit.add(getNameOfBranch(branch));
                }

                // add parents
                Collection<String> parentsOfCommit = new HashSet<>();
                for (int i = 0; i < commit.getParentCount(); ++i) {
                    parentsOfCommit.add(commit.getParent(i).getName());
                }
                parents.put(commit.getName(), parentsOfCommit);
            } else { // last tracked commit found
                GitCommit localHead = gitTrackingAccess.getHead(getNameOfBranch(branch));
                if (localHead == null) { // branch has no commits yet
                    benchmarkedCommitsNotInBranch.add(commit.getName());
                } else if (!localHead.getCommitHash().equals(commit.getName())) { // check for force push
                    throw new ForcePushException();
                } else { // all new commits from branch found
                    break;
                }
            }
        }
        return untrackedCommits;
    }

    private Repository getRepository(String path) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repository = repositoryBuilder.setGitDir(
                new File(path + "/.git"))
                .readEnvironment()
                .findGitDir()
                .setMustExist(true)
                .build();
        return repository;
    }


    /**
     * Clones a repository to the current working directory.
     * @param gitRepository is the repository being cloned.
     * @throws GitAPIException if the Git authentication fails.
     */
    public void cloneRepository(@NotNull GitRepository gitRepository) throws GitAPIException {
        Objects.requireNonNull(gitRepository);

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

    public static String getNameOfBranch(Ref branch) {
        // remove the "refs/remotes/origin/" part
        return branch.getName().substring(20);
    }

}
