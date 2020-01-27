package pacr.webapp_backend.git_tracking.endpoints;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.git_tracking.services.GitTracking;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Pavel Zwerschke
 */
@RestController
public class TestController {

    private GitTracking gitTracking;

    public TestController(GitTracking gitTracking) {
        this.gitTracking = gitTracking;
    }

    @RequestMapping("/testGitTracking/Lean")
    public void testGitTrackingLean() {
        int repoID = gitTracking.addRepository("git@github.com:leanprover/lean4.git", null, "Lean",
                new HashSet<>(Arrays.asList("master", "NewParserAttr", "test")));

        gitTracking.pullFromRepository(repoID);

        System.out.println("Finished\n\n\n");
    }

    @RequestMapping("/testGitTracking/Mijs")
    public void testGitTrackingMijs() {
        int repoID = gitTracking.addRepository("git@git.scc.kit.edu:qa2270/mjis-mirror.git", null, "mijs",
                new HashSet<>(Arrays.asList("master")));

        gitTracking.pullFromRepository(repoID);
        System.out.println("Finished\n\n\n");
    }

    @RequestMapping("/testGitTracking/Test")
    public void testGitTrackinTest() {
        int repoID = gitTracking.addRepository("git@git.scc.kit.edu:pacr/pacr-test-repository.git", null, "pacr test repo",
                new HashSet<>(Arrays.asList("master")));

        gitTracking.pullFromRepository(repoID);
        System.out.println("Finished\n\n\n");
    }

}
