package pacr.webapp_backend.git_tracking;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
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

    @OneToMany(fetch = FetchType.EAGER)
    private Set<GitCommit> commits;

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
    public Collection<GitCommit> getCommits() {
        return commits;
    }

    /**
     * Adds a commit to this branch.
     * @param commit is the commit being added.
     */
    public void addCommit(@NotNull GitCommit commit) {
        Objects.requireNonNull(commit);

        if (commits.contains(commit)) {
            return;
        }
        this.commits.add(commit);
        commit.addBranch(this);
    }
}
