package pacr.webapp_backend.dashboard_management;

import lombok.NoArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

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
@NoArgsConstructor
@Getter
public abstract class DashboardModule {

    @Id
    @GeneratedValue
    private int id;

    @Override
    public boolean equals(final Object o) {

        return o != null && this.getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
