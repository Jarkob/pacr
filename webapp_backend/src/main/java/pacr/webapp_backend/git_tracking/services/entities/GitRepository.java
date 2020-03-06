package pacr.webapp_backend.git_tracking.services.entities;

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
import java.util.List;
import java.util.Arrays;
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
public class GitRepository {

    private static final String MASTER = "master";

    @Id
    // When a repository id is set, it is not 0 anymore, it is an integer greater than 0.
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private boolean trackAllBranches;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<GitBranch> trackedBranches;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> selectedBranches;

    private String pullURL;
    private String name;
    private boolean isHookSet;
    private String color;
    private LocalDate observeFromDate;
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
     */
    @Deprecated
    public GitRepository(boolean trackAllBranches,
                         @NotNull String pullURL, @NotNull String name,
                         @NotNull String color, LocalDate observeFromDate) {
        Objects.requireNonNull(pullURL);
        Objects.requireNonNull(name);
        Objects.requireNonNull(color);

        this.selectedBranches = new HashSet<>();
        this.trackedBranches = new HashSet<>();

        this.trackAllBranches = trackAllBranches;
        this.pullURL = pullURL;
        this.name = name;
        this.isHookSet = false;
        this.color = color;
        this.observeFromDate = observeFromDate;
        setCommitLinkPrefix(pullURL);
    }

    /**
     * Returns the repository ID.
     * @return repository id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the repository ID.
     * @param id is the repository id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns whether all branches or just the master branch are being tracked.
     * @return true if all branches are being tracked, false if just the master branch.
     */
    public boolean isTrackAllBranches() {
        return trackAllBranches;
    }

    /**
     * Gets the commit link prefix of the repository.
     * @return the prefix of the URL directing to the commit.
     */
    public String getCommitLinkPrefix() {
        return commitLinkPrefix;
    }

    /**
     * Returns all selected branches. These are all tracked branches if isTrackAllBranches returns true or
     * all ignored branches if isTrackAllBranches returns false.
     * @return selected Branches
     */
    public Collection<GitBranch> getTrackedBranches() {
        return trackedBranches;
    }

    /**
     * Sets the pull URL for this repository.
     * @param pullURL is the pull URL.
     */
    public void setPullURL(@NotNull String pullURL) {
        Objects.requireNonNull(pullURL);

        setCommitLinkPrefix(pullURL);

        this.pullURL = pullURL;
    }

    private void setCommitLinkPrefix(String pullURL) {
        List<String> gitPrefixes = Arrays.asList("git@github.com", "git@gitlab.com", "git@git.scc.kit.edu");

        for (String prefix : gitPrefixes) {
            if (pullURL.startsWith(prefix)) {
                this.commitLinkPrefix = getCommitURLPrefix(pullURL, prefix);
            }
        }
    }

    private String getCommitURLPrefix(String pullURL, String gitPrefix) {
        int startIndex = pullURL.indexOf(':') + 1; // ':' is the separator for host and repository path
        int endIndex = pullURL.indexOf(".git");
        String gitHttpsPrefix = "https://" + gitPrefix.substring(4) + "/";
        String repositoryInfix = pullURL.substring(startIndex, endIndex);
        String commitSuffix = "/commit/";
        return gitHttpsPrefix + repositoryInfix + commitSuffix;
    }

    /**
     * Returns the pull URL for the repository.
     * @return pull URL
     */
    public String getPullURL() {
        return pullURL;
    }

    /**
     * Sets the name of the repository.
     * @param name is the name of the repository.
     */
    public void setName(@NotNull String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    /**
     * Returns the name of the repository.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether a WebHook is set for this repository or not.
     * @return true if a WebHook is set, false if no WebHook is set.
     */
    public boolean isHookSet() {
        return isHookSet;
    }

    /**
     * Returns the color in which the repository is displayed.
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the date from which on the repository is being observed. All commits before that date are ignored.
     * @return date
     */
    public LocalDate getObserveFromDate() {
        return observeFromDate;
    }

    /**
     * Removes a branch from the selected branches.
     * @param branch is the branch being removed.
     */
    public void removeBranchFromSelection(@NotNull GitBranch branch) {
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
    public boolean isBranchSelected(@NotNull String branchName) {
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
    public GitBranch getTrackedBranch(@NotNull String branchName) {
        Objects.requireNonNull(branchName);

        for (GitBranch branch : trackedBranches) {
            if (branch.getName().equals(branchName)) {
                return branch;
            }
        }

    throw new NoSuchElementException("Branch " + branchName + " does not exist.");
    }

    public void createBranchIfNotExists(@NotNull String branchName) {
        Objects.requireNonNull(branchName);

        for (GitBranch branch : trackedBranches) {
            if (branch.getName().equals(branchName)) {
                return;
            }
        }

        GitBranch newBranch = new GitBranch(branchName);
        trackedBranches.add(newBranch);
    }

    /**
     * @param trackAllBranches is the options whether all branches are tracked or only master branch.
     */
    public void setTrackAllBranches(boolean trackAllBranches) {
        this.trackAllBranches = trackAllBranches;
    }

    /**
     * @param hookSet is whether a hook is set for the repository.
     */
    public void setIsHookSet(boolean hookSet) {
        this.isHookSet = hookSet;
    }

    /**
     * @param color is the new color of the repository.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @param observeFromDate is the date from which on the commits are being tracked.
     */
    public void setObserveFromDate(LocalDate observeFromDate) {
        this.observeFromDate = observeFromDate;
    }

    public Set<String> getSelectedBranches() {
        return selectedBranches;
    }

    public void setSelectedBranches(Set<String> selectedBranches) {
        this.selectedBranches = selectedBranches;

        trackedBranches.removeIf(branch -> !isBranchSelected(branch.getName()));
    }
}
