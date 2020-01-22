package pacr.webapp_backend.git_tracking;

import jdk.jshell.spi.ExecutionControl;
import pacr.webapp_backend.shared.ICommit;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column
    private LocalDateTime entryDate;
    @Column
    private LocalDateTime commitDate;
    @Column
    private LocalDateTime authorDate;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<GitCommit> parents;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private GitRepository repository;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> labels;

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<GitBranch> branches;

    /**
     * Creates a commit.
     * @param commitHash is the hash of the commit.
     * @param commitMessage is the commit message.
     * @param commitDate is the commit date.
     * @param authorDate is the author date.
     * @param repository is the repository this commit belongs to.
     */
    public GitCommit(@NotNull String commitHash, @NotNull String commitMessage, @NotNull LocalDateTime commitDate,
                     @NotNull LocalDateTime authorDate,
                     @NotNull GitRepository repository) {
        Objects.requireNonNull(commitHash);
        Objects.requireNonNull(commitMessage);
        Objects.requireNonNull(commitDate);
        Objects.requireNonNull(authorDate);
        Objects.requireNonNull(repository);

        this.commitHash = commitHash;
        this.commitMessage = commitMessage;
        this.entryDate = LocalDateTime.now();
        this.commitDate = commitDate;
        this.authorDate = authorDate;
        this.labels = new HashSet<String>();
        this.branches = new HashSet<>();
        this.parents = new HashSet<>();

        this.repository = repository;
        repository.addNewCommit(this);
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
    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    @Override
    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    @Override
    public LocalDateTime getAuthorDate() {
        return authorDate;
    }

    @Override
    public Collection<GitCommit> getParents() {
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
    public void addBranch(@NotNull GitBranch branch) {
        Objects.requireNonNull(branch);

        if (branches.contains(branch)) {
            return;
        }
        branches.add(branch);
        branch.addCommit(this);
    }

    /**
     * Returns the branches this commit belongs to.
     * @return branches.
     */
    public Collection<GitBranch> getBranches() {
        return branches;
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

    @Override
    public Collection<String> getBranchNames() {
        return null;//todo
    }

    /**
     * Checks if the repository of this commit is stored in the database.
     * @return true if the repository is stored in the database, false if it isn't.
     */
    public boolean repositoryIsInDatabase() {
        return repository.isInDatabase();
    }

    public void addParent(GitCommit commit) {
        parents.add(commit);
    }

}
