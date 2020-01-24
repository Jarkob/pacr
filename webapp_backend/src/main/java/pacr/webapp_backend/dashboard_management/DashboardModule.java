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

    static final int SIZE = Dashboard.SIZE;


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
     * if the position is in the range [0,{@value #SIZE - 1}]
     *
     * @param position The new position
     */
    public void setPosition(int position) {
        if (position < SIZE && position >= 0) {
            this.position = position;
        } else {
            throw new IllegalArgumentException("The position " + position + " is not in the vaild range "
                    + "[0," + (SIZE - 1) + "]");
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

    @Override
    public boolean equals(Object o) {

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        DashboardModule dm = (DashboardModule) o;
        return dm.getPosition() == this.getPosition();
    }

}
