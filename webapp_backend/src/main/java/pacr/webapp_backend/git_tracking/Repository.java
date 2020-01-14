package pacr.webapp_backend.git_tracking;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.awt.Color;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class Repository {

    @Id
    private int id;

    private boolean trackAllBranches;

    @OneToMany
    private Collection<Branch> selectedBranches;

    @OneToMany
    private Map<String, Commit> commits;
    private String pullURL;
    private String name;
    private boolean isHookSet;
    private Color color;
    private LocalDate observeFromDate;

    /**
     * Creates an empty repository. Necessary to be an Entity.
     */
    public Repository() {
    }

    /**
     * Creates a new repository.
     * @param trackAllBranches is whether all branches are being tracked.
     * @param selectedBranches are the selected branches.
     * @param pullURL is the pull URL of the repository.
     * @param name is the name of the repository.
     * @param color is the color in which the repository is displayed
     * @param observeFromDate is the date from which on the repository is being observed.
     */
    public Repository(boolean trackAllBranches, @NotNull Collection<Branch> selectedBranches, @NotNull String pullURL,
               @NotNull String name, @NotNull Color color, @NotNull LocalDate observeFromDate) {
        this.trackAllBranches = trackAllBranches;
        if (selectedBranches == null) {
            throw new IllegalArgumentException("selectedBranches must not be null.");
        }
        this.selectedBranches = selectedBranches;
        this.commits = new HashMap<>();
        if (pullURL == null) {
            throw new IllegalArgumentException("pullURL must not be null.");
        }
        this.pullURL = pullURL;
        if (name == null) {
            throw new IllegalArgumentException("name must not be null.");
        }
        this.name = name;
        this.isHookSet = false;
        if (color == null) {
            throw new IllegalArgumentException("color must not be null.");
        }
        this.color = color;
        if (observeFromDate == null) {
            throw new IllegalArgumentException("observeFromDate must not be null.");
        }
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
    public Collection<Branch> getSelectedBranches() {
        return selectedBranches;
    }

    /**
     * Sets the pull URL for this repository.
     * @param pullURL is the pull URL.
     */
    public void setPullURL(String pullURL) {
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
    public void setName(String name) {
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
    public void addBranchToSelection(Branch branch) {
        selectedBranches.add(branch);
    }

    /**
     * Removes a branch from the selected branches.
     * @param branch is the branch being removed.
     */
    public void removeBranchFromSelection(Branch branch) {
        selectedBranches.remove(branch);
    }

    /**
     * Adds a new commit to this repository.
     * @param commit is the commit being added.
     */
    public void addNewCommit(@NotNull Commit commit) {
        if (commit == null) {
            throw new IllegalArgumentException("commit must not be null.");
        }
        if (this.commits.containsValue(commit)) {
            return;
        }
        this.commits.put(commit.getCommitHash(), commit);
        commit.setRepository(this);
    }
}
