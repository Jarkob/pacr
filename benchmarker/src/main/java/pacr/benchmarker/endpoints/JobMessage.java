package pacr.benchmarker.endpoints;

/**
 * Represents a job message from the Web-App.
 * Contains a repository pull URL and a commit hash.
 *
 * @author Pavel Zwerschke
 */
public class JobMessage {

    private String repository;
    private String commitHash;

    /**
     * Creates a new instance of JobMessage.
     */
    public JobMessage() {
    }

    /**
     * Creates an instance of JobResult.
     * @param repository is the pull URL of the repository.
     * @param commitHash is the commit hash.
     */
    public JobMessage(String repository, String commitHash) {
        this.repository = repository;
        this.commitHash = commitHash;
    }

    /**
     * @return the pull URL of the repository.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @return the commit hash of the JobMessage.
     */
    public String getCommitHash() {
        return commitHash;
    }

}
