package pacr.webapp_backend.dashboard_management;

/**
 * This abstract class defines the basic structure of any type of dashboard module.
 * An instance of any subclass this class has, models a dashboard module with a
 * defined position on a dashboard.
 * This class contains a fitting set-method for this position.
 *
 * @author Benedikt Hahn
 */
abstract class DashboardModule {

    //In range [0,14]
    int position = -1;

    /**
     * Sets the position of this dashboard module on its dashboard,
     * if the position is in the range [0,14]
     *
     * @param position The new position
     */
    void setPosition(int position) {
        if (position <= 14 && position >= 0) {
            this.position = position;
        } else {
            throw new IllegalArgumentException("The position " + position + " is not in the vaild range [0,14]");
        }
    }

    /**
     * @return The position of this module on its dashboard.
     */
    int getPosition() {
        return this.position;
    }
}
