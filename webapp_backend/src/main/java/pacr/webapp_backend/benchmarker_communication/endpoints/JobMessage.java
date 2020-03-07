package pacr.webapp_backend.benchmarker_communication.endpoints;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a message to a PACR-Benchmarker that is sent over a websocket connection.
 * The message tells the benchmarker which commit needs to be benchmarked.
 */
@NoArgsConstructor
@Getter
public class JobMessage {

    private String repository;

    private String commitHash;

    /**
     * Creates a new JobMessage with a commit and the repository it belongs to.
     * @param repository the repository pull-url.
     * @param commitHash the commit hash.
     */
    JobMessage(String repository, String commitHash) {
        this.repository = repository;
        this.commitHash = commitHash;
    }

}
