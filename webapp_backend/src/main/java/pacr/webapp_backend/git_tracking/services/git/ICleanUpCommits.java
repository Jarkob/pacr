package pacr.webapp_backend.git_tracking.services.git;

import org.eclipse.jgit.api.Git;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Selects all commits that don't belong to the selected branches of the git tree.
 *
 * @author Pavel Zwerschke
 */
public interface ICleanUpCommits {

    /**
     * Cleans up unused commits after a force push.
     * @param git is the Git where the force push happened.
     * @param gitRepository is the GitRepository where the force push happened.
     * @param gitTrackingAccess is the access to the DB.
     * @return all commits that are not needed any more.
     */
    Set<String> cleanUp(@NotNull Git git, @NotNull GitRepository gitRepository,
                        @NotNull IGitTrackingAccess gitTrackingAccess);

}
