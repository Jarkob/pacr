package pacr.webapp_backend.shared;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * This class provides an interface for using abstract leaderboards from other components,
 * than the leaderboard component.
 * It should be an interface. Only because of technical limitations regarding storing it in the database,
 * we decide do make it an abstract class.
 *
 * @author Benedikt Hahn
 */
@Entity
public abstract class ILeaderboard {

    @Id
    @GeneratedValue
    protected int id;

    /**
     * Returns the repository ID.
     * @return repository id
     */
    public abstract int getId();
}
