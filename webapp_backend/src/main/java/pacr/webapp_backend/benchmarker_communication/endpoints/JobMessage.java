package pacr.webapp_backend.benchmarker_communication.endpoints;

/**
 * Represents a message to a PACR-Benchmarker that is sent over a websocket connection.
 * The message tells the benchmarker which commit needs to be benchmarked.
 */
public class JobMessage {

    private String repository;

    private String commitHash;

    /**
     * Creates an empty JobMessage.
     *
     * Needed for Spring to work.
     */
    public JobMessage() {
    }

    /**
     * Creates a new JobMessage with a commit and the repository it belongs to.
     * @param repository the repository pull-url.
     * @param commitHash the commit hash.
     */
    JobMessage(String repository, String commitHash) {
        assert (repository != null && !repository.isBlank() && !repository.isEmpty());
        assert (commitHash != null && !commitHash.isBlank() && !commitHash.isEmpty());

        this.repository = repository;
        this.commitHash = commitHash;
    }

    /**
     * @return the repository pull-url.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @return the commit hash.
     */
    public String getCommitHash() {
        return commitHash;
    }
}
