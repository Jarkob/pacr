package pacr.webapp_backend.dashboard_management.services;

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
    private int position;

    DashboardModule() {
        position = -1;
    }

    DashboardModule(int position) {
        setPosition(position);
    }

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
        int pos = this.position;
        if (pos < 0) {
            throw new IllegalStateException("The position of this dashboard module has not been set yet.");
        }
        return this.position;
    }

    /**
     * Returns whether this object is equal to another object, meaning that they are of the same class
     * and have the same attributes.
     * @param o The object, tto which this object should be compared.
     * @return {@code false} if the objects are not equal and {@code true} if the object is equal to this one.
     */
    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }

        DashboardModule dm = (DashboardModule) o;
        return dm.getPosition() == this.getPosition();
    }

}
