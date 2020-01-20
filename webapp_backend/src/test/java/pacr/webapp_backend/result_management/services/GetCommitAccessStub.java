package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.git_tracking.GitBranch;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.awt.Color;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class GetCommitAccessStub implements IGetCommitAccess {
    public static final String HASH = "hash";
    public static final String MSG = "msg";
    public static final LocalDate DATE = LocalDate.now();
    public static final String URL = "url";
    public static final String REPO_NAME = "repo";
    public static final Color COLOR = Color.BLACK;
    public static final String BRANCH_NAME = "master";

    private GitCommit commit;

    GetCommitAccessStub() {
        GitBranch branch = new GitBranch(BRANCH_NAME);
        LinkedList<GitBranch> branches = new LinkedList<>();
        branches.add(branch);
        GitRepository repository = new GitRepository(false, branches, URL, REPO_NAME, COLOR, DATE);
        this.commit = new GitCommit(HASH, MSG, DATE, DATE, new HashSet<>(), repository, branch);
    }

    @Override
    public Collection<? extends ICommit> getCommitsFromRepository(int id) {
        LinkedList<GitCommit> commits = new LinkedList<>();
        commits.add(commit);
        return commits;
    }

    @Override
    public Collection<? extends ICommit> getCommitsFromBranch(int id, @NotNull String branch) {
        LinkedList<GitCommit> commits = new LinkedList<>();
        commits.add(commit);
        return commits;
    }

    @Override
    public Collection<? extends ICommit> getAllCommits() {
        LinkedList<GitCommit> commits = new LinkedList<>();
        commits.add(commit);
        return commits;
    }

    @Override
    public ICommit getCommit(@NotNull String commitHash) {
        return commit;
    }
}
