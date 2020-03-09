package pacr.webapp_backend.git_tracking.services.entities;

import lombok.NoArgsConstructor;
import pacr.webapp_backend.shared.ICommit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;
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
@NoArgsConstructor
public class GitCommit implements ICommit {

    private static final int MAX_STRING_LENGTH = 2000;

    @Id
    private String commitHash;

    @Column(length = MAX_STRING_LENGTH)
    private String commitMessage;
    private LocalDateTime entryDate;
    private LocalDateTime commitDate;
    private LocalDateTime authorDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> parentHashes;

    @ManyToOne(fetch = FetchType.EAGER)
    private GitRepository repository;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> labels;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<GitBranch> branches;


    /**
     * Creates a commit.
     * @param commitHash is the hash of the commit.
     * @param commitMessage is the commit message.
     * @param commitDate is the commit date.
     * @param authorDate is the author date.
     * @param repository is the repository this commit belongs to.
     */
    public GitCommit(@NotNull final String commitHash, @NotNull final String commitMessage,
                     @NotNull final LocalDateTime commitDate, @NotNull final LocalDateTime authorDate,
                     @NotNull final GitRepository repository) {
        Objects.requireNonNull(commitHash);
        Objects.requireNonNull(commitMessage);
        Objects.requireNonNull(commitDate);
        Objects.requireNonNull(authorDate);
        Objects.requireNonNull(repository);

        this.commitHash = commitHash;

        this.commitMessage = commitMessage;
        if (commitMessage.length() > MAX_STRING_LENGTH) {
            this.commitMessage = commitMessage.substring(0, MAX_STRING_LENGTH);
        }

        this.entryDate = LocalDateTime.now();
        this.commitDate = commitDate;
        this.authorDate = authorDate;
        this.labels = new HashSet<>();
        this.branches = new HashSet<>();
        this.parentHashes = new HashSet<>();

        this.repository = repository;
    }

    @Override
    public String getCommitHash() {
        return commitHash;
    }

    @Override
    public String getCommitMessage() {
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
    public Collection<String> getParentHashes() {
        return parentHashes;
    }

    @Override
    public int getRepositoryID() {
        return repository.getId();
    }

    @Override
    public String getRepositoryName() {
        return repository.getName();
    }

    @Override
    public boolean isOnMaster() {
        for (final GitBranch branch : branches) {
            if (branch.isMaster()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the branch for this commit.
     * @param branch is the branch being set.
     */
    public void addBranch(@NotNull final GitBranch branch) {
        Objects.requireNonNull(branch);

        branches.add(branch);
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
    public void setRepository(@NotNull final GitRepository repository) {
        Objects.requireNonNull(repository);

        if (repository == this.repository) {
            return;
        }
        this.repository = repository;
    }

    @Override
    public void addLabel(@NotNull final String label) {
        Objects.requireNonNull(label);

        labels.add(label);
    }

    @Override
    public void removeLabel(@NotNull final String label) {
        Objects.requireNonNull(label);

        labels.remove(label);
    }

    @Override
    public Collection<String> getLabels() {
        return labels;
    }

    @Override
    public Collection<String> getBranchNames() {
        final Collection<String> branchNames = new HashSet<>();
        for (final GitBranch branch : branches) {
            branchNames.add(branch.getName());
        }
        return branchNames;
    }

    @Override
    public String getCommitURL() {
        return repository.getCommitLinkPrefix() + commitHash;
    }

    /**
     * Checks if the repository of this commit is stored in the database.
     * @return true if the repository is stored in the database, false if it isn't.
     */
    public boolean repositoryIsInDatabase() {
        return repository.isInDatabase();
    }

    /**
     * Adds a parent hash to this commit.
     * @param commitHash is the parent hash.
     */
    public void addParent(@NotNull final String commitHash) {
        Objects.requireNonNull(commitHash);

        parentHashes.add(commitHash);
    }

    public boolean isOnBranch(final String branchName) {
        return getBranchNames().contains(branchName);
    }

}
