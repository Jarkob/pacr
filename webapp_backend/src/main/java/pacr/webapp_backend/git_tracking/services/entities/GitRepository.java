package pacr.webapp_backend.git_tracking.services.entities;

import lombok.Getter;
import lombok.Setter;
import pacr.webapp_backend.shared.IRepository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * This class represents a repository.
 * It contains a unique ID, an option whether all branches
 * are being tracked or just the master branch.
 * All selected branches that are being tracked/ignored,
 * commit belonging to the repository, a Pull-URL,
 * a name, an option whether a WebHook is set for this repository,
 * a color and a date from which the repository is being observed.
 *
 * @author Pavel Zwerschke
 */
@Entity
public class GitRepository implements IRepository {

    @Id
    // When a repository id is set, it is not 0 anymore, it is an integer greater than 0.
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter @Setter
    private int id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Getter
    private Set<GitBranch> trackedBranches;

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    private Set<String> selectedBranches;

    @Getter
    private String pullURL;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private boolean trackAllBranches;
    @Getter @Setter
    private boolean hookSet;
    @Getter @Setter
    private String color;
    @Getter @Setter
    private LocalDate observeFromDate;
    @Getter
    private String commitLinkPrefix;

    /**
     * Creates an empty repository. Necessary to be an Entity.
     */
    public GitRepository() {
        this.trackedBranches = new HashSet<>();
        this.selectedBranches = new HashSet<>();
        this.commitLinkPrefix = null;
    }

    /**
     * Creates a new repository.
     * @param trackAllBranches is whether all branches are being tracked.
     * @param pullURL is the pull URL of the repository.
     * @param name is the name of the repository.
     * @param color is the color in which the repository is displayed
     * @param observeFromDate is the date from which on the repository is being observed.
     *                        Is null if all commits are being observed.
     *
     * @deprecated since it doesn't get used anymore.
     */
    @Deprecated
    public GitRepository(final boolean trackAllBranches,
                         @NotNull final String pullURL, @NotNull final String name,
                         @NotNull final String color, final LocalDate observeFromDate) {
        Objects.requireNonNull(pullURL);
        Objects.requireNonNull(name);
        Objects.requireNonNull(color);

        this.selectedBranches = new HashSet<>();
        this.trackedBranches = new HashSet<>();

        this.trackAllBranches = trackAllBranches;
        this.pullURL = pullURL;
        this.name = name;
        this.hookSet = false;
        this.color = color;
        this.observeFromDate = observeFromDate;
        setCommitLinkPrefix();
    }

    /**
     * Sets the pull URL for this repository.
     * @param pullURL is the pull URL.
     */
    public void setPullURL(@NotNull final String pullURL) {
        Objects.requireNonNull(pullURL);

        this.pullURL = pullURL;

        setCommitLinkPrefix();
    }

    private void setCommitLinkPrefix() {
        int startIndex = pullURL.indexOf(':') + 1; // ':' is the separator for host and repository path
        int endIndex = pullURL.indexOf(".git");
        if (startIndex >= 1 && startIndex < endIndex && pullURL.length() > endIndex) {
            String gitHttpsPrefix = "https://" + pullURL.substring(4, startIndex - 1) + "/";
            String repositoryInfix = pullURL.substring(startIndex, endIndex);
            String commitSuffix = "/commit/";

            this.commitLinkPrefix = gitHttpsPrefix + repositoryInfix + commitSuffix;
        }
    }

    @Override
    public Set<String> getTrackedBranchNames() {
        Set<String> branchNames = new HashSet<>();
        for (GitBranch branch : trackedBranches) {
            branchNames.add(branch.getName());
        }

        return branchNames;
    }

    /**
     * Removes a branch from the selected branches.
     * @param branch is the branch being removed.
     */
    public void removeBranchFromSelection(@NotNull final GitBranch branch) {
        Objects.requireNonNull(trackedBranches);

        trackedBranches.remove(branch);
    }

    /**
     * Checks if the repository is added to the database. In that case, the ID is not 0 anymore.
     * @return true if it is in the database, false if it isn't.
     */
    public boolean isInDatabase() {
        return id > 0;
    }

    /**
     * Checks if a branch is selected or not.
     * @param branchName is the name of the branch.
     * @return true if the branch is selected, false if not.
     */
    public boolean isBranchSelected(@NotNull final String branchName) {
        Objects.requireNonNull(branchName);

        if (trackAllBranches) {
            return !selectedBranches.contains(branchName);
        }

        return selectedBranches.contains(branchName);
    }

    /**
     * Gets a tracked GitBranch of this repository.
     * @param branchName is the name of the branch.
     * @return GitBranch.
     */
    public GitBranch getTrackedBranch(@NotNull final String branchName) {
        Objects.requireNonNull(branchName);

        for (final GitBranch branch : trackedBranches) {
            if (branch.getName().equals(branchName)) {
                return branch;
            }
        }

        throw new NoSuchElementException("Branch " + branchName + " does not exist.");
    }

    /**
     * Creates a branch if it doesn't exist yet.
     * @param branchName is the branch name.
     */
    public void createBranchIfNotExists(@NotNull String branchName) {
        Objects.requireNonNull(branchName);

        for (final GitBranch branch : trackedBranches) {
            if (branch.getName().equals(branchName)) {
                return;
            }
        }

        final GitBranch newBranch = new GitBranch(branchName);
        trackedBranches.add(newBranch);
    }

    /**
     * @param selectedBranches are the new selected branches.
     */
    public void setSelectedBranches(Set<String> selectedBranches) {
        this.selectedBranches = selectedBranches;

        trackedBranches.removeIf(branch -> !isBranchSelected(branch.getName()));
    }
}
