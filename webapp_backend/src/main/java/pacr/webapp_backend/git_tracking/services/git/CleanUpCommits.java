package pacr.webapp_backend.git_tracking.services.git;

import lombok.NoArgsConstructor;
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
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of ICleanUpCommits.
 *
 * @author Pavel Zwerschke
 */
@Component
@NoArgsConstructor
public class CleanUpCommits implements ICleanUpCommits {

    private static final Logger LOGGER = LogManager.getLogger(CleanUpCommits.class);

    @Override
    public Set<String> cleanUp(@NotNull final Git git, @NotNull final GitRepository gitRepository,
                               @NotNull final IGitTrackingAccess gitTrackingAccess) {
        Objects.requireNonNull(git);
        Objects.requireNonNull(gitRepository);

        // initialize map that stores whether the commit is used or not
        final Collection<String> commitHashes = gitTrackingAccess.getAllCommitHashes(gitRepository.getId());
        final Map<String, Boolean> commitUsed = new HashMap<>();
        for (final String commitHash : commitHashes) {
            commitUsed.put(commitHash, Boolean.FALSE);
        }

        // get branches
        final List<Ref> branches;
        try {
            branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        } catch (final GitAPIException e) {
            LOGGER.error("Could not get branch list of repository.");
            return new HashSet<>();
        }
        for (final Ref branch : branches) {
            // only selected branches must be checked for commits
            if (gitRepository.isBranchSelected(GitHandler.getNameOfRef(branch))) {
                final Iterable<RevCommit> commits;
                try {
                    commits = git.log().add(branch.getObjectId()).call();
                } catch (final MissingObjectException | IncorrectObjectTypeException e) {
                    // should not happen because branch is retrieved from branchlist
                    LOGGER.error("branch was wrong object type or not available in git.");
                    break;
                } catch (final NoHeadException e) {
                    LOGGER.error("No head for branch {}.", branch.getName());
                    break;
                } catch (final GitAPIException e) {
                    LOGGER.error("Could not get commits for branch {}.", branch.getName());
                    break;
                }

                for (final RevCommit commit : commits) {
                    if (commitUsed.containsKey(commit.getName())) {
                        commitUsed.put(commit.getName(), Boolean.TRUE);
                    }
                }
            }
        }

        // get all unused commits
        final Set<String> toDelete = new HashSet<>();
        for (final Map.Entry<String, Boolean> entry : commitUsed.entrySet()) {
            if (!entry.getValue()) {
                toDelete.add(entry.getKey());
            }
        }

        LOGGER.info("Found {} commits to delete.", toDelete.size());

        return toDelete;
    }

}
