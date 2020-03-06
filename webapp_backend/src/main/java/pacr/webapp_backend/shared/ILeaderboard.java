package pacr.webapp_backend.shared;


import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * This class provides an interface for using abstract leaderboards from other components,
 * than the leaderboard component.
 * It should be an interface. Only because of technical limitations regarding storing it in the database,
 * we decide do make it an abstract class.
 *
 * @author Benedikt Hahn
 */
//TODO
@Entity
public abstract class ILeaderboard {

    @Id
    private String benchmarkName;

}
