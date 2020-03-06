package pacr.webapp_backend.integration;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.authentication.endpoints.LoginController;
import pacr.webapp_backend.authentication.services.Password;
import pacr.webapp_backend.authentication.services.PasswordCreator;
import pacr.webapp_backend.authentication.services.Token;
import pacr.webapp_backend.git_tracking.endpoints.RepositoryManagerController;
import pacr.webapp_backend.git_tracking.endpoints.TransferRepository;
import pacr.webapp_backend.git_tracking.services.GitTracking;
import pacr.webapp_backend.scheduler.endpoints.SchedulerController;
import pacr.webapp_backend.scheduler.services.Job;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
public class GitTrackingScheduler extends SpringBootTestWithoutShell {

    private static final String MASTER_BRANCH = "master";
    private static final String PULL_URL = "git@git.scc.kit.edu:uuwig/pacr-private-test.git";
    private static final String REPO_NAME = "PACR test";
    private static final String COMMIT_PREFIX = "https://git.scc.kit.edu/uuwig/pacr-private-test/-/commit/";
    private static final String COLOR = "#ffffff";
    private static final LocalDate NEW_YEAR_2020 = LocalDate.of(2020, 1, 1);
    private static final int EXPECTED_SINGLE = 1;

    @Autowired
    private RepositoryManagerController repositoryManagerController;
    @Autowired
    private LoginController loginController;
    @Autowired
    private PasswordCreator passwordCreator;
    @Autowired
    private GitTracking gitTracking;
    @Autowired
    private SchedulerController schedulerController;

    @Test
    @DatabaseSetup
    public void addRepositories_shouldBeIncludedInAllRepositories() {
        String password = passwordCreator.newPassword();
        Password pw = new Password(password);
        ResponseEntity<Token> token = loginController.login(pw);

        List<String> trackedBranches = Arrays.asList(MASTER_BRANCH);
        Collections.sort(trackedBranches);
        TransferRepository repo = new TransferRepository(0, true, trackedBranches,
                PULL_URL, REPO_NAME, false,
                COLOR, NEW_YEAR_2020, COMMIT_PREFIX);

        repositoryManagerController.addRepository(repo, token.getBody().getToken());

        List<TransferRepository> allRepos = repositoryManagerController.getAllRepositories();

        assertEquals(EXPECTED_SINGLE, allRepos.size());
        assertEquals(REPO_NAME, allRepos.get(0).getName());

        gitTracking.pullFromAllRepositories();

        Page<Job> jobs = schedulerController.getJobsQueue(PageRequest.of(0, 10));

        assertEquals(EXPECTED_SINGLE, jobs.getContent().size());
    }
}
