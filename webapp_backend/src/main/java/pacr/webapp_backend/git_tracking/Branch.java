package pacr.webapp_backend.git_tracking;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class represents a branch.
 * It contains a name and commits belonging to the branch.
 *
 * @author Pavel Zwerschke
 */
@Entity
public class Branch {

    @Id
    private int id;

    private String name;

    @OneToMany
    private Collection<Commit> commits;

    /**
     * Creates an empty branch. Necessary to be an Entity.
     */
    public Branch() {
    }

    /**
     * Creates a new branch.
     * @param name is the name of the branch.
     */
    public Branch(@NotNull String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null.");
        }
        this.name = name;
        this.commits = new HashSet<>();
    }

    /**
     * Returns the name of this branch.
     * @return branch name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all commits in this branch.
     * @return commits
     */
    public Collection<Commit> getCommits() {
        return commits;
    }

    /**
     * Adds a commit to this branch.
     * @param commit is the commit being added.
     */
    public void addCommit(@NotNull Commit commit) {
        if (commit == null) {
            throw new IllegalArgumentException("commit must not be null.");
        }
        if (commits.contains(commit)) {
            return;
        }
        this.commits.add(commit);
        commit.setBranch(this);
    }
}
