package pacr.webapp_backend.git_tracking.endpoints;

import java.time.LocalDate;
import java.util.Set;

/**
 * Transfer object for encoding repositories in JSON.
 *
 * @author Pavel Zwerschke
 */
public class TransferRepository {

    private int id;
    private boolean trackAllBranches;
    private Set<String> selectedBranches;
    private String pullURL;
    private String name;
    private boolean isHookSet;
    private String color;
    private LocalDate observeFromDate;
    private String commitLinkPrefix;

    /**
     * Creates a new instance of TransferRepository.
     * @param id is the ID of the repository.
     * @param trackAllBranches is the option whether all branches are being tracked.
     * @param selectedBranches are all selected branches.
     * @param pullURL is the pull URL of the repository.
     * @param name is the name of the repository.
     * @param isHookSet is the option whether a hook is set.
     * @param color is the color of the repository encoded in hex.
     * @param observeFromDate is the date from which on the repository is observed.
     * @param commitLinkPrefix is the commit link prefix.
     */
    public TransferRepository(int id, boolean trackAllBranches, Set<String> selectedBranches, String pullURL,
                              String name, boolean isHookSet, String color, LocalDate observeFromDate,
                              String commitLinkPrefix) {
        this.id = id;
        this.trackAllBranches = trackAllBranches;
        this.selectedBranches = selectedBranches;
        this.pullURL = pullURL;
        this.name = name;
        this.isHookSet = isHookSet;
        this.color = color;
        this.observeFromDate = observeFromDate;
        this.commitLinkPrefix = commitLinkPrefix;
    }

    /**
     * @return whether all branches are being tracked.
     */
    public boolean isTrackAllBranches() {
        return trackAllBranches;
    }

    /**
     * @param trackAllBranches is the option whether all branches are being tracked.
     */
    public void setTrackAllBranches(boolean trackAllBranches) {
        this.trackAllBranches = trackAllBranches;
    }

    /**
     * @return all selected branches.
     */
    public Set<String> getSelectedBranches() {
        return selectedBranches;
    }

    /**
     * @param branchNames are all selected branches.
     */
    public void setSelectedBranches(Set<String> branchNames) {
        this.selectedBranches = branchNames;
    }

    /**
     * @return the pull URL of the repository.
     */
    public String getPullURL() {
        return pullURL;
    }

    /**
     * @param pullURL is the pull URL of the repository.
     */
    public void setPullURL(String pullURL) {
        this.pullURL = pullURL;
    }

    /**
     * @return the name of the repository.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name is the name of the repository.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return whether a hook is set or not for this repository.
     */
    public boolean isHookSet() {
        return isHookSet;
    }

    /**
     * @param hookSet is whether a hook is set or not for this repository.
     */
    public void setHookSet(boolean hookSet) {
        isHookSet = hookSet;
    }

    /**
     * @return the color of this repository.
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color is the color of this repository.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the date from which the commits are being observed.
     */
    public LocalDate getObserveFromDate() {
        return observeFromDate;
    }

    /**
     * @param observeFromDate is the date from which the commits are being observed.
     */
    public void setObserveFromDate(LocalDate observeFromDate) {
        this.observeFromDate = observeFromDate;
    }

    /**
     * @return the commit link prefix.
     */
    public String getCommitLinkPrefix() {
        return commitLinkPrefix;
    }

    /**
     * @param commitLinkPrefix is the commit link prefix.
     */
    public void setCommitLinkPrefix(String commitLinkPrefix) {
        this.commitLinkPrefix = commitLinkPrefix;
    }

    /**
     * @return the ID for this repository.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id is the ID for this repository.
     */
    public void setId(int id) {
        this.id = id;
    }
}
