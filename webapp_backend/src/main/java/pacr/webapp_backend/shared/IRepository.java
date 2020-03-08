package pacr.webapp_backend.shared;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.Set;

/**
 * An interface for a git repository.
 */
@Entity
public abstract class IRepository {

    @Id
    // When a repository id is set, it is not 0 anymore, it is an integer greater than 0.
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected int id;

    /**
     * Returns the name of the repository.
     * @return name
     */
    public abstract String getName();

    /**
     * Returns the pull URL for the repository.
     * @return pull URL
     */
    public abstract String getPullURL();

    /**
     * @return all tracked branches
     */
    public abstract Set<String> getTrackedBranchNames();

    /**
     * Returns the repository ID.
     * @return repository id
     */
    public abstract int getId();
}
