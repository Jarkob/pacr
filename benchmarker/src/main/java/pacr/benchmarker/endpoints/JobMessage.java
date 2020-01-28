package pacr.benchmarker.endpoints;

/**
 * @author Pavel Zwerschke
 */
public class JobMessage {

    private String repository;

    private String commitHash;

    public JobMessage() {
    }

    public JobMessage(String repository, String commitHash) {
        this.repository = repository;
        this.commitHash = commitHash;
    }

    public String getRepository() {
        return repository;
    }

    public String getCommitHash() {
        return commitHash;
    }

}
