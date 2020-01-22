package pacr.webapp_backend.git_tracking;

import javassist.NotFoundException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import javax.validation.constraints.NotNull;
import java.awt.Color;
import java.time.LocalDate;
import java.util.*;

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
    // When a repository id is set, is is not 0 anymore, it is an integer greater than 0.
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private boolean trackAllBranches;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<GitBranch> selectedBranches;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<String, GitCommit> commits;
    private String pullURL;
    private String name;
    private boolean isHookSet;
    private Color color;
    private LocalDate observeFromDate;

    /**
     * Creates an empty repository. Necessary to be an Entity.
     */
    public GitRepository() {
        this.commits = new HashMap<>();
        this.selectedBranches = new HashSet<>();
    }

    /**
     * Creates a new repository.
     * @param trackAllBranches is whether all branches are being tracked.
     * @param selectedBranches are the selected branches.
     * @param pullURL is the pull URL of the repository.
     * @param name is the name of the repository.
     * @param color is the color in which the repository is displayed
     * @param observeFromDate is the date from which on the repository is being observed.
     *                        Is null if all commits are being observed.
     */
    public GitRepository(boolean trackAllBranches, @NotNull Collection<GitBranch> selectedBranches,
                         @NotNull String pullURL, @NotNull String name,
                         @NotNull Color color, LocalDate observeFromDate) {
        Objects.requireNonNull(selectedBranches);
        Objects.requireNonNull(pullURL);
        Objects.requireNonNull(name);
        Objects.requireNonNull(color);

        this.trackAllBranches = trackAllBranches;
        this.selectedBranches = selectedBranches;
        this.commits = new HashMap<>();
        this.pullURL = pullURL;
        this.name = name;
        this.isHookSet = false;
        this.color = color;
        this.observeFromDate = observeFromDate;
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
     * Returns all selected branches. These are all tracked branches if isTrackAllBranches returns true or
     * all ignored branches if isTrackAllBranches returns false.
     * @return selected Branches
     */
    public Collection<GitBranch> getSelectedBranches() {
        return selectedBranches;
    }

    /**
     * Sets the pull URL for this repository.
     * @param pullURL is the pull URL.
     */
    public void setPullURL(@NotNull String pullURL) {
        Objects.requireNonNull(pullURL);

        this.pullURL = pullURL;
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
    public Color getColor() {
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
     * Changes the settings of this repository so that all branches are being tracked.
     */
    public void trackAllBranches() {
        trackAllBranches = true;
    }

    /**
     * Changes the settings of this repository so that only the master branch is being tracked.
     */
    public void trackOnlyMasterBranch() {
        trackAllBranches = false;
    }

    /**
     * Adds a branch to the selected branches.
     * @param branch is the branch being added.
     */
    public void addBranchToSelection(@NotNull GitBranch branch) {
        Objects.requireNonNull(branch);

        selectedBranches.add(branch);
    }

    /**
     * Removes a branch from the selected branches.
     * @param branch is the branch being removed.
     */
    public void removeBranchFromSelection(@NotNull GitBranch branch) {
        Objects.requireNonNull(selectedBranches);

        selectedBranches.remove(branch);
    }

    /**
     * Adds a new commit to this repository.
     * @param commit is the commit being added.
     */
    public void addNewCommit(@NotNull GitCommit commit) {
        Objects.requireNonNull(commit);

        if (this.commits.containsValue(commit)) {
            return;
        }
        this.commits.put(commit.getCommitHash(), commit);
        commit.setRepository(this);
    }

    /**
     * Checks if the repository is added to the database. In that case, the ID is not 0 anymore.
     * @return true if it is in the database, false if it isn't.
     */
    public boolean isInDatabase() {
        return id > 0;
    }

    public boolean isBranchSelected(String branchName) {
        if (branchName.equals(MASTER)) {
            return true;
        }

        if (isTrackAllBranches()) { // selectedBranches are ignored branches
            for (GitBranch branch : selectedBranches) {
                if (branch.getName().equals(branchName)) {
                    return false;
                }
            }
            return true;
        } else { // selected branches are watched branches
            for (GitBranch branch : selectedBranches) {
                if (branch.getName().equals(branchName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public GitBranch getSelectedBranch(String branchName) throws NotFoundException {
        for (GitBranch branch : selectedBranches) {
            if (branch.getName().equals(branchName)) {
                return branch;
            }
        }
        throw new NotFoundException("Branch " + branchName + " was not found.");
    }
}
