package pacr.benchmarker.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a job message from the Web-App.
 * Contains a repository pull URL and a commit hash.
 *
 * @author Pavel Zwerschke
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobMessage {

    private String repository;
    private String commitHash;

}
