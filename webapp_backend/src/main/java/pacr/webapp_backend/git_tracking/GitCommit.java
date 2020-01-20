package pacr.webapp_backend.git_tracking;

import pacr.webapp_backend.shared.ICommit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
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
public class GitCommit implements ICommit {

    /**
     * Creates an empty commit. Necessary to be an Entity.
     */
    public GitCommit() {
    }

    @Id
    private String commitHash;

    private String commitMessage;
    private LocalDate entryDate;
    private LocalDate commitDate;
    private LocalDate authorDate;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<GitCommit> parents;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private GitRepository repository;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> labels;

    @ManyToOne(cascade = CascadeType.ALL)
    private GitBranch branch;

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
    public GitCommit(@NotNull String commitHash, @NotNull String commitMessage, @NotNull LocalDate commitDate,
                     @NotNull LocalDate authorDate, @NotNull Set<GitCommit> parents,
                     @NotNull GitRepository repository, @NotNull GitBranch branch) {
        Objects.requireNonNull(commitHash);
        Objects.requireNonNull(commitMessage);
        Objects.requireNonNull(commitDate);
        Objects.requireNonNull(authorDate);
        Objects.requireNonNull(parents);
        Objects.requireNonNull(repository);
        Objects.requireNonNull(branch);

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
    public void setBranch(@NotNull GitBranch branch) {
        Objects.requireNonNull(branch);

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
    public GitBranch getBranch() {
        return branch;
    }

    /**
     * Sets the repository for this commit.
     * @param repository is the repository being added.
     */
    public void setRepository(@NotNull GitRepository repository) {
        Objects.requireNonNull(repository);

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
        Objects.requireNonNull(label);
        labels.add(label);
    }

    @Override
    public void removeLabel(String label) {
        //Objects.requireNonNull(label);

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
