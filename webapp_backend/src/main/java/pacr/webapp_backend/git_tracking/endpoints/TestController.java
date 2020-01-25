package pacr.webapp_backend.git_tracking.endpoints;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.git_tracking.services.GitTracking;

import java.util.Arrays;

/**
 * @author Pavel Zwerschke
 */
@RestController
public class TestController {

    private GitTracking gitTracking;

    public TestController(GitTracking gitTracking) {
        this.gitTracking = gitTracking;
    }

    @RequestMapping("/testGitTrackingLean")
    public void testGitTrackingLean() {
        int repoID = gitTracking.addRepository("git@github.com:leanprover/lean4.git", null, "Lean",
                Arrays.asList("master", "NewParserAttr", "test"));

        gitTracking.pullFromRepository(repoID);

        System.out.println("Finished\n\n\n");
    }

    @RequestMapping("/testGitTrackingMijs")
    public void testGitTrackingMijs() {
        int repoID = gitTracking.addRepository("git@git.scc.kit.edu:qa2270/mjis-mirror.git", null, "mijs",
                Arrays.asList("master"));

        gitTracking.pullFromRepository(repoID);
        System.out.println("Finished\n\n\n");
    }

}
