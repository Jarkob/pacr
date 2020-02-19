package pacr.webapp_backend.git_tracking.services.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
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

    private static final String MASTER_BRANCH_NAME = "master";

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
     * Returns the head hash of this branch.
     * @return commit hash
     */
    public String getHeadHash() {
        return headHash;
    }

    /**
     * Sets the head hash of this branch.
     * @param headHash is the hash of the head.
     */
    public void setHeadHash(String headHash) {
        this.headHash = headHash;
    }

    /**
     * @return {@code true} if this branch is the master branch, otherwise {@code false}.
     */
    public boolean isMaster() {
        return name.equals(MASTER_BRANCH_NAME);
    }
}
