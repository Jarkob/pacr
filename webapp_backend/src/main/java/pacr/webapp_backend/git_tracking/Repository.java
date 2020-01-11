package pacr.webapp_backend.git_tracking;

import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Repository {

    private int id;
    private boolean trackAllBranches;
    private Collection<Branch> selectedBranches;
    private Map<String, Commit> commits;
    private String pullURL;
    private String name;
    private boolean isHookSet;
    private Color color;
    private Date observeFromDate;

    /**
     * Creates a new repository.
     * @param id is the repository id.
     * @param trackAllBranches is whether all branches are being tracked.
     * @param selectedBranches are the selected branches.
     * @param pullURL is the pull URL of the repository.
     * @param name is the name of the repository.
     * @param color is the color in which the repository is displayed
     * @param observeFromDate is the date from which on the repository is being observed.
     */
    Repository(int id, boolean trackAllBranches, Collection<Branch> selectedBranches, String pullURL,
                      String name, Color color, Date observeFromDate) {
        this.id = id;
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
     * Returns the pull URL for the repository.
     * @return pull URL
     */
    public String getPullURL() {
        return pullURL;
    }

    /**
     * Returns the name of the repository.
     * @return name
     */
    public String getName() {
        return name;
    }

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
    public Date getObserveFromDate() {
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

    public void addNewCommit(Commit commit) {
        this.commits.put(commit.getHash(), commit);
    }
}
