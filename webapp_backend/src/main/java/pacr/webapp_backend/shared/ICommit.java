package pacr.webapp_backend.shared;

import java.util.Collection;
import java.util.Date;

public interface ICommit {

    /**
     * Returns the commit hash for this commit.
     * @return commit hash
     */
    public String getHash();

    /**
     * Returns the commit message for this commit.
     * @return commit message
     */
    public String getMessage();

    /**
     * Returns the date when the commit got entered into the system.
     * @return entry date
     */
    public Date getEntryDate();

    /**
     * Returns the commit date for this commit.
     * @return commit date
     */
    public Date getCommitDate();

    /**
     * Returns the author date for this commit.
     * @return author date
     */
    public Date getAuthorDate();

    /**
     * Returns the parents for this commit. Is usually just one commit.
     * @return parents
     */
    public Collection<ICommit> getParents();

    /**
     * Returns the ID for the corresponding repository.
     * @return repository id
     */
    public int getRepositoryID();

    /**
     * Returns the name of the branch this commit is belonging to.
     * @return branch name
     */
    public String getBranchName();

    /**
     * Returns the labels of the commit. This includes Git-Tags and PACR-tags.
     * @return labels
     */
    public Collection<String> getLabels();

}
