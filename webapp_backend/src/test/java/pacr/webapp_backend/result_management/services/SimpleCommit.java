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
    public LocalDateTime getEntryDate() {
        return null;
    }

    @Override
    public LocalDateTime getCommitDate() {
        return null;
    }

    @Override
    public LocalDateTime getAuthorDate() {
        return null;
    }

    @Override
    public Collection<String> getParentHashes() {
        return null;
    }

    @Override
    public int getRepositoryID() {
        return 0;
    }

    @Override
    public String getRepositoryName() {
        return null;
    }

    @Override
    public Collection<String> getBranchNames() {
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
