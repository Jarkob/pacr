package pacr.webapp_backend.dashboard_management.services;


import javax.validation.constraints.NotNull;

/**
 * Instances of this class model dashboards, which contain dashboard modules
 * and are displayed to the user in the frontend.
 * Those instances furthermore have a title and contain edit and view keys,
 * with which they can be retrieved and edited from the frontend.
 *
 * Public methods of this class can be used to remove and add modules,
 * as well as edit keys.
 *
 * @author Benedikt Hahn
 */
class Dashboard {

    static int MIN_POSITION = 0;
    static int MAX_POSITION = 14;


    private String editKey;
    private String viewKey;

    private String title;

    //Limited to 15 positions on the dashboard.
    private DashboardModule[] modules = new DashboardModule[MAX_POSITION - MIN_POSITION  + 1];


    /**
     * @param title The title of the dashboard.
     */
    Dashboard(String title) {
        this.title = title;
    }

    /**
     * Adds a module to the dashboard, if its position is valid and
     * its position is not occupied.
     *
     * @param module    The module that will be added.
     */
    void addModule(@NotNull DashboardModule module) {

        if (module == null) {
            throw new IllegalArgumentException("Dashboard module must not be null.");
        }

        int position = MIN_POSITION - 1;

        try {
            position = module.getPosition();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (modules[position] != null) {
            throw new IllegalArgumentException("The given dashboard module position "
                                                + position + " is already occupied.");
        }

        modules[position] = module;
    }

    /**
     * Removes the specified module from the dashboard and returns whether the remove operation
     * was successful.
     * @param module The module to be removed.
     * @return {@code true} if the module could be removed and {@code false} else.
     */
    boolean removeModule(@NotNull DashboardModule module) {
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
    boolean removeModule(int position) {
        if (position < MIN_POSITION || position > MAX_POSITION) {
            throw new IllegalArgumentException("Dashboards only allow positioning in the range "
                    + "[" + MIN_POSITION + "," + MAX_POSITION + "].");
        }
        if (modules[position] == null) {
            return false;
        }
        modules[position] = null;
        return true;
    }


    /**
     * Sets the edit key of this dashboard.
     *
     * @param editKey The new edit key.
     */
    void setEditKey(String editKey) {
        this.editKey = editKey;
    }

    /**
     * Sets the view key of this dashboard.
     *
     * @param viewKey The new view key.
     */
    void setViewKey(String viewKey) {
        this.viewKey = viewKey;
    }


}
