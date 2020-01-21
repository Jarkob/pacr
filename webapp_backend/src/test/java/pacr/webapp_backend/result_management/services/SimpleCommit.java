package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.ICommit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Simple ICommit implementation for testing purposes.
 */
public class SimpleCommit implements ICommit {
    private String commitHash;
    private String message;
    private LocalDateTime entryDate;
    private LocalDateTime commitDate;
    private LocalDateTime authorDate;
    private Collection<SimpleCommit> parents;

    @Override
    public String getCommitHash() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public LocalDate getEntryDate() {
        return null;
    }

    @Override
    public LocalDate getCommitDate() {
        return null;
    }

    @Override
    public LocalDate getAuthorDate() {
        return null;
    }

    @Override
    public Collection<? extends ICommit> getParents() {
        return null;
    }

    @Override
    public int getRepositoryID() {
        return 0;
    }

    @Override
    public String getBranchName() {
        return null;
    }

    @Override
    public void addLabel(String label) {
    }

    @Override
    public void removeLabel(String label) {
    }

    @Override
    public Collection<String> getLabels() {
        return null;
    }
}
