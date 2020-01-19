package pacr.webapp_backend.dashboard_management;

import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This abstract class defines the basic structure of any type of dashboard module.
 * An instance of any subclass this class has, models a dashboard module with a
 * defined position on a dashboard.
 * This class contains a fitting set-method for this position.
 *
 * @author Benedikt Hahn
 */
@Entity
@Configurable
abstract class DashboardModule {

    @Id
    @GeneratedValue
    private int id;

    static int MIN_POSITION = Dashboard.MIN_POSITION;
    static int MAX_POSITION = Dashboard.MAX_POSITION;


    //In range [0,14]
    private int position;

    public DashboardModule() {
        position = -1;
    }

    DashboardModule(int position) {
        setPosition(position);
    }

    /**
     * Sets the position of this dashboard module on its dashboard,
     * if the position is in the range [{@value #MIN_POSITION},{@value #MAX_POSITION}]
     *
     * @param position The new position
     */
    public void setPosition(int position) {
        if (position <= MAX_POSITION && position >= MIN_POSITION) {
            this.position = position;
        } else {
            throw new IllegalArgumentException("The position " + position + " is not in the vaild range ["
                    + MIN_POSITION + "," + MAX_POSITION + "]");
        }
    }

    /**
     * @return The position of this module on its dashboard.
     */
    public int getPosition() {
        int pos = this.position;
        if (pos < MIN_POSITION) {
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
