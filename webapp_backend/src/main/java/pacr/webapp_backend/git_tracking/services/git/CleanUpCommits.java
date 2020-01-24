package pacr.webapp_backend.git_tracking.services.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

/**
 * Implementation of ICleanUpCommits.
 *
 * @author Pavel Zwerschke
 */
@Component
public class CleanUpCommits implements ICleanUpCommits {

    private static final Logger LOGGER = LogManager.getLogger(CleanUpCommits.class);

    private IGitTrackingAccess gitTrackingAccess;

    public CleanUpCommits(@NotNull IGitTrackingAccess gitTrackingAccess) {
        Objects.requireNonNull(gitTrackingAccess);

        this.gitTrackingAccess = gitTrackingAccess;
    }

    public Collection<String> cleanUp(@NotNull Git git, @NotNull GitRepository gitRepository) {
        Objects.requireNonNull(git);
        Objects.requireNonNull(gitRepository);

        // initialize map that stores whether the commit is used or not
        Collection<String> commitHashes = gitTrackingAccess.getAllCommitHashes(gitRepository.getId());
        Map<String, Boolean> commitUsed = new HashMap<>();
        for (String commitHash : commitHashes) {
            commitUsed.put(commitHash, Boolean.FALSE);
        }

        // get branches
        List<Ref> branches = null;
        try {
            branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        } catch (GitAPIException e) {
            LOGGER.error("Could not get branch list of repository.");
            return new HashSet<>();
        }
        for (Ref branch : branches) {
            // only selected branches must be checked for commits
            if (gitRepository.isBranchSelected(GitHandler.getNameOfBranch(branch))) {
                Iterable<RevCommit> commits = null;
                try {
                    commits = git.log().add(branch.getObjectId()).call();
                } catch (MissingObjectException | IncorrectObjectTypeException e) {
                    // should not happen because branch is retrieved from branchlist
                    LOGGER.error("branch was wrong object type or not available in git.");
                    break;
                } catch (NoHeadException e) {
                    LOGGER.error("No head for branch {}.", branch.getName());
                    break;
                } catch (GitAPIException e) {
                    LOGGER.error("Could not get commits for branch {}.", branch.getName());
                    break;
                }

                for (RevCommit commit : commits) {
                    if (commitUsed.containsKey(commit.getName())) {
                        commitUsed.put(commit.getName(), Boolean.TRUE);
                    }
                }
            }
        }

        // get all unused commits
        Collection<String> toDelete = new HashSet<>();
        for (Map.Entry<String, Boolean> entry : commitUsed.entrySet()) {
            if (!entry.getValue()) {
                toDelete.add(entry.getKey());
            }
        }

        return toDelete;
    }

}
