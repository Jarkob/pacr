package pacr.webapp_backend.dashboard_management;


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

    String editKey;
    String viewKey;

    String title;

    //Limited to 15 positions on the dashboard.
    DashboardModule[] modules = new DashboardModule[15];


    /**
     * @param title The title of the dashboard.
     */
    Dashboard(String title) {
        this.title = title;
    }

    /**
     * Adds a module to the dashboard, if its position is valid and
     *
     * @param module    The module that will be added.
     */
    void addModule(DashboardModule module) {
        int position = module.getPosition();

        if (position == -1) {
            throw new IllegalArgumentException("The position of the module must be set "
                    + "to a value in the validRange [0,14].");
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
    boolean removeModule(DashboardModule module) {
        int position = module.getPosition();
        return removeModule(position);
    }

    /**
     * Removes the module at the given position and returns whether the remove operation
     * was successful.
     * @param position The position to remove the module from.
     * @return {@code true} if the module at position could be removed and {@code false} else.
     */
    boolean removeModule(int position) {
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
