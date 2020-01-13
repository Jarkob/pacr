package pacr.webapp_backend.benchmarker_communication.services;

/**
 * A benchmarking job that has a PACR-Benchmarker associated to it.
 */
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
    public BenchmarkerJob(String address, String repository, String commitHash) {
        verifyAddress(address);
        verifyRepository(repository);
        verifyCommitHash(commitHash);

        this.address = address;
        this.repository = repository;
        this.commitHash = commitHash;
    }

    private void verifyAddress(String address) {
        if (address == null || address.isEmpty() || address.isBlank()) {
            throw new IllegalArgumentException("The address is not valid.");
        }
    }

    private void verifyRepository(String repository) {
        if (repository == null || repository.isEmpty() || repository.isBlank()) {
            throw new IllegalArgumentException("The repository pull-url is not valid.");
        }
    }

    private void verifyCommitHash(String commitHash) {
        if (commitHash == null || commitHash.isEmpty() || commitHash.isBlank()) {
            throw new IllegalArgumentException("The commit hash is not valid.");
        }
    }

    /**
     * Creates an empty BenchmarkerJob.
     *
     * Needed for Spring to work.
     */
    public BenchmarkerJob() {
    }

    /**
     * @return the address of the benchmarker that gets the job.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the pull-url of the repository the commit belongs to.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @return the commit hash of the commit which gets benchmarked.
     */
    public String getCommitHash() {
        return commitHash;
    }
}
