package pacr.webapp_backend.git_tracking;

import pacr.webapp_backend.shared.ICommit;

import java.util.Collection;
import java.util.HashSet;

public class Branch {

    private String name;
    private Collection<ICommit> commits;

    /**
     * Creates a new branch.
     * @param name is the name of the branch.
     */
    Branch(String name) {
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
    public Collection<ICommit> getCommits() {
        return commits;
    }

    /**
     * Adds a commit to this branch.
     * @param commit is the commit being added.
     */
    public void addCommit(ICommit commit) {
        this.commits.add(commit);
    }
}
