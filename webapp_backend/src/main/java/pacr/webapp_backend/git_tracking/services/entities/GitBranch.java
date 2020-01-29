package pacr.webapp_backend.git_tracking.services.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * This class represents a branch.
 * It contains a name and commits belonging to the branch.
 *
 * @author Pavel Zwerschke
 */
@Entity
public class GitBranch {

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String headHash;

    /**
     * Creates an empty branch. Necessary to be an Entity.
     */
    public GitBranch() {
    }

    /**
     * Creates a new branch.
     * @param name is the name of the branch.
     */
    public GitBranch(@NotNull String name) {
        Objects.requireNonNull(name);

        this.name = name;
        this.headHash = null;
    }

    /**
     * Returns the name of this branch.
     * @return branch name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the head of this branch.
     * @return GitCommit todo
     */
    public String getHeadHash() {
        return headHash;
    }

    /**
     * Sets the head of this branch.
     * @param headHash is the hash of the head. todo
     */
    public void setHeadHash(String headHash) {
        this.headHash = headHash;
    }
}
