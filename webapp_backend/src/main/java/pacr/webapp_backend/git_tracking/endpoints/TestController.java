package pacr.webapp_backend.git_tracking.endpoints;

import javassist.NotFoundException;
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
    private int repoID;

    public TestController(GitTracking gitTracking) {
        this.gitTracking = gitTracking;
        repoID = gitTracking.addRepository("git@github.com:leanprover/lean4.git", null, "TestRepo",
                Arrays.asList("master"));
    }

    @RequestMapping("/testGitTracking")
    public void testGitTracking() {
        try {
            gitTracking.pullFromRepository(repoID);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Finished\n\n\n");
    }

}
