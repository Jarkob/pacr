package pacr.webapp_backend.git_tracking;

import pacr.webapp_backend.shared.ICommit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
@Entity
public class Commit implements ICommit {

    /**
     * Creates an empty commit. Necessary to be an Entity.
     */
    public Commit() {
    }

    @Id
    private String commitHash;

    private String commitMessage;
    private LocalDate entryDate;
    private LocalDate commitDate;
    private LocalDate authorDate;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Commit> parents;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Repository repository;

    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<String> labels;

    @ManyToOne(cascade = CascadeType.ALL)
    private Branch branch;

    /**
     * Creates a commit.
     * @param commitHash is the hash of the commit.
     * @param commitMessage is the commit message.
     * @param commitDate is the commit date.
     * @param authorDate is the author date.
     * @param parents are the parents of the commit. Is usually just one commit.
     * @param repository is the repository this commit belongs to.
     * @param branch is the corresponding branch.
     */
    public Commit(String commitHash, String commitMessage, LocalDate commitDate, LocalDate authorDate,
                  Set<Commit> parents, Repository repository, Branch branch) {
        this.commitHash = commitHash;
        this.commitMessage = commitMessage;
        this.entryDate = LocalDate.now();
        this.commitDate = commitDate;
        this.authorDate = authorDate;
        this.parents = parents;
        this.labels = new HashSet<String>();

        this.repository = repository;
        repository.addNewCommit(this);
        this.branch = branch;
        branch.addCommit(this);
    }

    @Override
    public String getCommitHash() {
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
        return repository.getId();
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

    /**
     * Returns the branch this commit belongs to.
     * @return branch.
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Sets the repository for this commit.
     * @param repository is the repository being added.
     */
    public void setRepository(Repository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null.");
        }
        if (repository == this.repository) {
            return;
        }
        this.repository = repository;
        repository.addNewCommit(this);
    }

    @Override
    public String getBranchName() {
        return branch.getName();
    }

    @Override
    public void addLabel(String label) {
        labels.add(label);
    }

    @Override
    public void removeLabel(String label) {
        labels.remove(label);
    }

    @Override
    public Collection<String> getLabels() {
        return labels;
    }

    /**
     * Checks if the repository of this commit is stored in the database.
     * @return true if the repository is stored in the database, false if it isn't.
     */
    public boolean repositoryIsInDatabase() {
        return repository.isInDatabase();
    }

}
