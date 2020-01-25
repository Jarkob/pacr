package pacr.webapp_backend.shared;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Represents a commit.
 * Can return the commit hash, the commit message,
 * the entry date, the commit date, the author date,
 * the parents of the commit, the repository ID of the
 * repository the commit is belonging to, the name
 * of the branch the commit is belonging to and
 * the labels of the commit.
 *
 * @author Pavel Zwerschke
 */
public interface ICommit {

    /**
     * Returns the commit hash for this commit.
     * @return commit hash
     */
    String getCommitHash();

    /**
     * Returns the commit message for this commit.
     * @return commit message
     */
    String getMessage();

    /**
     * Returns the date when the commit got entered into the system.
     * @return entry date
     */
    LocalDateTime getEntryDate();

    /**
     * Returns the commit date for this commit.
     * @return commit date
     */
    LocalDateTime getCommitDate();

    /**
     * Returns the author date for this commit.
     * @return author date
     */
    LocalDateTime getAuthorDate();

    /**
     * Returns the parents for this commit. Is usually just one commit.
     * @return parents
     */
    Collection<String> getParentHashes();

    /**
     * Returns the ID for the corresponding repository.
     * @return repository id
     */
    int getRepositoryID();

    /**
     * @return the name for the corresponding repository
     */
    String getRepositoryName();

    /**
     * Adds a label to the commit.
     * @param label is the label being added.
     */
    void addLabel(String label);

    /**
     * Removes a label from the commit.
     * @param label is the label being removed.
     */
    void removeLabel(String label);

    /**
     * Returns the labels of the commit. This includes Git-Tags and PACR-tags.
     * @return labels
     */
    Collection<String> getLabels();

    /**
     * Gets all branch names.
     * @return branch names.
     */
    Collection<String> getBranchNames();
}
