package pacr.webapp_backend.benchmarker_communication.services;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A benchmarking job that has a PACR-Benchmarker associated to it.
 */
@NoArgsConstructor
@Getter
public class BenchmarkerJob {

    private String address;
    private String repository;
    private String commitHash;

    /**
     * Creates a new BenchmarkerJob.
     * @param address the address of the benchmarker that gets the job.
     * @param repository the pull-url of the repository the commit belongs to.
     * @param commitHash the commit hash of the commit which gets benchmarked.
     */
    public BenchmarkerJob(final String address, final String repository, final String commitHash) {
        verifyAddress(address);
        verifyRepository(repository);
        verifyCommitHash(commitHash);

        this.address = address;
        this.repository = repository;
        this.commitHash = commitHash;
    }

    private void verifyAddress(final String address) {
        if (address == null || address.isEmpty() || address.isBlank()) {
            throw new IllegalArgumentException("The address is not valid.");
        }
    }

    private void verifyRepository(final String repository) {
        if (repository == null || repository.isEmpty() || repository.isBlank()) {
            throw new IllegalArgumentException("The repository pull-url is not valid.");
        }
    }

    private void verifyCommitHash(final String commitHash) {
        if (commitHash == null || commitHash.isEmpty() || commitHash.isBlank()) {
            throw new IllegalArgumentException("The commit hash is not valid.");
        }
    }

}
