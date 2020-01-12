package pacr.webapp_backend.git_tracking;

import pacr.webapp_backend.shared.ICommit;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class represents a commit.
 * It contains a commit hash, a commit message a commit date,
 * an author date, PACR-labels, parents and an entry date
 * (the date on which the commit got entered into the system).
 * It belongs to exactly one repository and has therefore a
 * unique repository id. The commit also belongs to exactly
 * one branch.
 *
 * @author Pavel Zwerschke
 */
public class Commit implements ICommit {

    private String commitHash;
    private String commitMessage;
    private LocalDate entryDate;
    private LocalDate commitDate;
    private LocalDate authorDate;
    private Collection<Commit> parents;
    private int repositoryID;
    private Collection<String> labels;
    private Branch branch;

    /**
     * Creates a commit.
     * @param commitHash is the hash of the commit.
     * @param commitMessage is the commit message.
     * @param commitDate is the commit date.
     * @param authorDate is the author date.
     * @param parents are the parents of the commit. Is usually just one commit.
     * @param repositoryID is the ID of the corresponding repository.
     * @param branch is the corresponding branch.
     */
    Commit(String commitHash, String commitMessage, LocalDate commitDate, LocalDate authorDate,
                  Collection<Commit> parents, int repositoryID, Branch branch) {
        this.commitHash = commitHash;
        this.commitMessage = commitMessage;
        this.entryDate = LocalDate.now();
        this.commitDate = commitDate;
        this.authorDate = authorDate;
        this.parents = parents;
        this.repositoryID = repositoryID;
        this.labels = new HashSet<String>();
        this.branch = branch;
    }

    @Override
    public String getHash() {
        return commitHash;
    }

    @Override
    public String getMessage() {
        return commitMessage;
    }

    @Override
    public LocalDate getEntryDate() {
        return entryDate;
    }

    @Override
    public LocalDate getCommitDate() {
        return commitDate;
    }

    @Override
    public LocalDate getAuthorDate() {
        return authorDate;
    }

    @Override
    public Collection<? extends ICommit> getParents() {
        return parents;
    }

    @Override
    public int getRepositoryID() {
        return repositoryID;
    }

    /**
     * Sets the branch for this commit.
     * @param branch is the branch being set.
     */
    public void setBranch(Branch branch) {
        if (branch == this.branch) {
            return;
        }
        this.branch = branch;
        branch.addCommit(this);
    }

    @Override
    public String getBranchName() {
        return branch.getName();
    }

    @Override
    public Collection<String> getLabels() {
        return labels;
    }
}
