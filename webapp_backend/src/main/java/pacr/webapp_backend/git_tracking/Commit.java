package pacr.webapp_backend.git_tracking;

import pacr.webapp_backend.shared.ICommit;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Commit implements ICommit {

    private String commitHash;
    private String commitMessage;
    private Date entryDate;
    private Date commitDate;
    private Date authorDate;
    private Collection<ICommit> parents;
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
    Commit(String commitHash, String commitMessage, Date commitDate, Date authorDate,
                  Collection<ICommit> parents, int repositoryID, Branch branch) {
        this.commitHash = commitHash;
        this.commitMessage = commitMessage;
        this.entryDate = new Date();
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
    public Date getEntryDate() {
        return entryDate;
    }

    @Override
    public Date getCommitDate() {
        return commitDate;
    }

    @Override
    public Date getAuthorDate() {
        return authorDate;
    }

    @Override
    public Collection<ICommit> getParents() {
        return parents;
    }

    @Override
    public int getRepositoryID() {
        return repositoryID;
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
