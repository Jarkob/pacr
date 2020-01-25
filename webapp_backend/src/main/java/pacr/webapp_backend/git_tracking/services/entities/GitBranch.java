package pacr.webapp_backend.git_tracking.services.entities;

import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
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

    @ManyToOne
    private GitCommit localHead;

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
        this.localHead = null;
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
     * @return GitCommit
     */
    public GitCommit getLocalHead() {
        return localHead;
    }

    /**
     * Sets the head of this branch.
     * @param localHead is the head.
     */
    public void setLocalHead(GitCommit localHead) {
        this.localHead = localHead;
    }
}
