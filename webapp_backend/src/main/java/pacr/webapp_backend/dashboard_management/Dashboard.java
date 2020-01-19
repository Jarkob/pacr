package pacr.webapp_backend.dashboard_management;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Instances of this class model dashboards, which contain dashboard modules
 * and are displayed to the user in the frontend.
 * Those instances furthermore have a title and contain edit and view keys,
 * with which they can be retrieved and edited from the frontend.
 *
 * Public methods of this class canbe used to remove and add modules,
 * as well as edit keys.
 *
 * @author Benedikt Hahn
 */
@Entity(name = "Dashboard")
@Table(name = "dashboard")
public class Dashboard {

    @Id
    @GeneratedValue
    private int id;

    static int MIN_POSITION = 0;
    static int MAX_POSITION = 14;

    private LocalDate lastAccess = LocalDate.now();

    /**
     * Default constructor used by jpa.
     */
    public Dashboard() {

    }

    private String editKey;
    private String viewKey;

    private String title;

    //Limited to 15 positions on the dashboard.
    @OneToMany(cascade = CascadeType.ALL)
    private List<DashboardModule> modules = Arrays.asList(new DashboardModule[MAX_POSITION - MIN_POSITION  + 1]);


    /**
     * Creates a new dashboard with a set title.
     * Throws an illegal argument exception, if the string is null, empty or blank.
     * @param title The title of the dashboard.
     */
    public Dashboard(@NotNull String title) {
        if (title == null || title.isEmpty() || title.isBlank()) {
            throw new IllegalArgumentException("The dashboard title '" + title + "' must not be null, empty or blank.");
        }
        this.title = title;
    }

    /**
     * Adds a module to the dashboard, if its position is valid and
     * its position is not occupied.
     * @param module    The module that will be added.
     */
    public void addModule(@NotNull DashboardModule module) {

        if (module == null) {
            throw new IllegalArgumentException("The dashboard module must not be null.");
        }

        int position = MIN_POSITION - 1;

        try {
            position = module.getPosition();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (modules.get(position) != null) {
            throw new IllegalArgumentException("The given dashboard module position "
                                                + position + " is already occupied.");
        }

        modules.set(position, module);
    }

    /**
     * Removes the specified module from the dashboard and returns whether the remove operation
     * was successful.
     * @param module The module to be removed.
     * @return {@code true} if the module could be removed and {@code false} else.
     */
    public boolean removeModule(@NotNull DashboardModule module) {
        if (module == null) {
            return false;
        }

        boolean moduleWasRemoved = false;

        for (DashboardModule dm : modules) {
            if (dm != null && dm.equals(module)) {
                moduleWasRemoved = removeModule(dm.getPosition());
                                //No two equal modules can be on the same dashboard,
                break;          // since their position makes them different.
            }
        }
        return moduleWasRemoved;
    }

    /**
     * Removes the module at the given position and returns whether the remove operation
     * was successful.
     * @param position The position to remove the module from.
     * @return {@code true} if the module at position could be removed and {@code false} else.
     */
    public boolean removeModule(int position) {
        if (position < MIN_POSITION || position > MAX_POSITION) {
            throw new IllegalArgumentException("Dashboards only allow positioning in the range "
                    + "[" + MIN_POSITION + "," + MAX_POSITION + "].");
        }
        if (modules.get(position) == null) {
            return false;
        }
        modules.set(position, null);
        return true;
    }


    /**
     * Sets the edit key of this dashboard.
     *
     * @param editKey The new edit key.
     */
    public void setEditKey(String editKey) {
        this.editKey = editKey;
    }

    /**
     * Sets the view key of this dashboard.
     *
     * @param viewKey The new view key.
     */
    public void setViewKey(String viewKey) {
        this.viewKey = viewKey;
    }


    /**
     * @return the edit key of this dashboard. The value can be null.
     */
    public String getEditKey() {
        return this.editKey;
    }

    /**
     * @return the view key of this dashboard. The value can be null.
     */
    public String getViewKey() {
        return viewKey;
    }

    /**
     * @return a collection of all leaderboard modules contained in this dashboard.
     */
    public Collection<LeaderboardDashboardModule> getLeaderboardModules() {

        //Get a collection of dashboard modules containing only leaderboard modules.
        ArrayList<DashboardModule> moduleList = new ArrayList<DashboardModule>(this.modules);
        moduleList.removeIf(
                (DashboardModule dm) -> dm == null || (dm.getClass() != LeaderboardDashboardModule.class));

        //Cast that collection to actual leaderboard modules.
        ArrayList<LeaderboardDashboardModule> leaderboardModuleList = new ArrayList<LeaderboardDashboardModule>();
        for (DashboardModule dm : moduleList) {
            leaderboardModuleList.add((LeaderboardDashboardModule) dm);
        }

        return leaderboardModuleList;
    }

    /**
     * Sets the last access to now.
     */
    public void updateLastAccess() {
        this.lastAccess = LocalDate.now();
    }

    /**
     * @return the last time this dashboard was accessed.
     */
    public LocalDate getLastAccess() {
        return lastAccess;
    }

    /**
     * @return the unique id of this dashboard.
     */
    public int getId() {
        return id;
    }
}
